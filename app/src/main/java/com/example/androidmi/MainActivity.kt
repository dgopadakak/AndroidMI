package com.example.androidmi

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmi.databinding.ActivityMainBinding
import com.example.androidmi.firms.Tour
import com.example.androidmi.firms.TravelCompany
import com.example.androidmi.firms.TravelCompanyOperator
import com.example.androidmi.firms.dbWithRoom.App
import com.example.androidmi.firms.dbWithRoom.AppDatabase
import com.example.androidmi.firms.dbWithRoom.TravelCompanyOperatorDao
import com.example.androidmi.forRecyclerView.CustomRecyclerAdapterForExams
import com.example.androidmi.forRecyclerView.RecyclerItemClickListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    TourDetailsDialogFragment.OnInputListenerSortId
{
    private val gsonBuilder = GsonBuilder()
    private val gson: Gson = gsonBuilder.create()
    private val serverIP = "192.168.1.69"
    private val serverPort = 9889
    private lateinit var connection: Connection
    private var connectionStage: Int = 0
    private var startTime: Long = 0

    private lateinit var db: AppDatabase
    private lateinit var pcoDao: TravelCompanyOperatorDao

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var nv: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewForStart: TextView
    private lateinit var recyclerViewExams: RecyclerView
    private lateinit var spinnerYears: Spinner
    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts
            .StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK)
        {
            val data: Intent? = result.data
            processOnActivityResult(data)
        }
    }

    private var tco: TravelCompanyOperator = TravelCompanyOperator()
    private var currentTravelCompanyID: Int = -1
    private var currentTourID: Int = -1
    private var currentYear: String = "Все годы"
    private var waitingForUpdate: Boolean = false
    private lateinit var groupTitle: String

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        nv = binding.navView
        nv.setNavigationItemSelectedListener(this)
        toolbar = findViewById(R.id.toolbar)
        toolbar.apply { setNavigationIcon(R.drawable.ic_my_menu) }
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        textViewForStart = findViewById(R.id.textViewForStart)
        textViewForStart.visibility = View.INVISIBLE
        progressBar = findViewById(R.id.progressBar)
        recyclerViewExams = findViewById(R.id.recyclerViewExams)
        recyclerViewExams.visibility = View.INVISIBLE
        recyclerViewExams.layoutManager = LinearLayoutManager(this)
        spinnerYears = findViewById(R.id.spinnerYears)
        spinnerYears.visibility = View.INVISIBLE

        recyclerViewExams.addOnItemTouchListener(
            RecyclerItemClickListener(
                recyclerViewExams,
                object : RecyclerItemClickListener.OnItemClickListener
                {
                    override fun onItemClick(view: View, position: Int)
                    {
                        currentTourID = position
                        Snackbar.make(findViewById(R.id.app_bar_main),
                            "Общая сумма: ${tco.getTour(currentTravelCompanyID, currentTourID)
                                .sum}", Snackbar.LENGTH_LONG)
                            .show()
                    }
                    override fun onItemLongClick(view: View, position: Int)
                    {
                        currentTourID = position
                        val examDetails = TourDetailsDialogFragment()
                        val tempExam = tco.getTour(currentTravelCompanyID, currentTourID)
                        val bundle = Bundle()
                        bundle.putString("name", tempExam.name)
                        bundle.putString("country", tempExam.country)
                        bundle.putString("number", tempExam.num.toString())
                        bundle.putString("dateOfSale", tempExam.dateOfSale)
                        bundle.putString("dateStart", tempExam.dateStart)
                        bundle.putString("sum", tempExam.sum.toString())
                        bundle.putString("isSoldOut", tempExam.isSoldOut.toString())
                        bundle.putString("comment", tempExam.comment)
                        bundle.putString("connection", connectionStage.toString())
                        examDetails.arguments = bundle
                        examDetails.show(fragmentManager, "MyCustomDialog")
                    }
                }
            )
        )

        val itemSelectedListener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
                {
                    if (currentTravelCompanyID != -1)
                    {
                        currentYear = parent.getItemAtPosition(position) as String
                        refreshBySpinner()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        spinnerYears.onItemSelectedListener = itemSelectedListener

        db = App.instance?.database!!
        pcoDao = db.groupOperatorDao()
        startTime = System.currentTimeMillis()
        connection = Connection(serverIP, serverPort, "REFRESH", this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean
    {
        if (currentTravelCompanyID != -1 && connectionStage == 1)
        {
            menu.getItem(0).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        if (id == R.id.action_add)
        {
            val intent = Intent()
            intent.setClass(this, EditTourActivity::class.java)
            intent.putExtra("action", 1)
            resultLauncher.launch(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    internal inner class Connection(
        private val SERVER_IP: String,
        private val SERVER_PORT: Int,
        private val refreshCommand: String,
        private val activity: Activity
    ) {
        private var outputServer: PrintWriter? = null
        private var inputServer: BufferedReader? = null
        var thread1: Thread? = null
        private var threadT: Thread? = null

        internal inner class Thread1Server : Runnable {
            override fun run()
            {
                val socket: Socket
                try {
                    socket = Socket(SERVER_IP, SERVER_PORT)
                    outputServer = PrintWriter(socket.getOutputStream())
                    inputServer = BufferedReader(InputStreamReader(socket.getInputStream()))
                    Thread(Thread2Server()).start()
                    sendDataToServer(refreshCommand)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        internal inner class Thread2Server : Runnable {
            override fun run() {
                while (true) {
                    try {
                        val message = inputServer!!.readLine()
                        if (message != null)
                        {
                            activity.runOnUiThread { processingInputStream(message) }
                        } else {
                            thread1 = Thread(Thread1Server())
                            thread1!!.start()
                            return
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        internal inner class Thread3Server(private val message: String) : Runnable
        {
            override fun run()
            {
                outputServer!!.write(message)
                outputServer!!.flush()
            }
        }

        internal inner class ThreadT : Runnable
        {
            override fun run() {
                while (true)
                {
                    if (System.currentTimeMillis() - startTime > 5000L && connectionStage == 0)
                    {
                        activity.runOnUiThread { Snackbar.make(findViewById(R.id.app_bar_main),
                            "Подключиться не удалось!\n" +
                                    "Будет использоваться локальная база данных.",
                            Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.YELLOW)
                            .show() }
                        connectionStage = -1
                        activity.runOnUiThread { progressBar.visibility = View.INVISIBLE
                            textViewForStart.visibility = View.VISIBLE }
                        tco = pcoDao.getById(1)
                        for (i in 0 until tco.getTravelCompanies().size)
                        {
                            activity.runOnUiThread { nv.menu.add(0, i, 0,
                                tco.getTravelCompanies()[i].name as CharSequence) }
                        }
                    }
                }
            }
        }

        fun sendDataToServer(text: String)
        {
            Thread(Thread3Server(text + "\n")).start()
        }

        private fun processingInputStream(text: String)
        {
            pcoDao.delete(TravelCompanyOperator())
            val tempGo: TravelCompanyOperator = gson
                .fromJson(text, TravelCompanyOperator::class.java)
            pcoDao.insert(tempGo)

            if (connectionStage != 1)
            {
                Snackbar.make(findViewById(R.id.app_bar_main),
                    "Успешно подключено!\n" +
                            "Будут использоваться серверные данные.",
                    Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.GREEN)
                    .show()
            }

            progressBar.visibility = View.INVISIBLE
            if (connectionStage == 0)
            {
                textViewForStart.visibility = View.VISIBLE
            }
            for (i in 0 until tco.getTravelCompanies().size)
            {
                nv.menu.removeItem(i)
            }
            val tempArrayListPharmacyChains: ArrayList<TravelCompany> = tempGo.getTravelCompanies()
            tco.setTravelCompanies(tempArrayListPharmacyChains)
            for (i in 0 until tempArrayListPharmacyChains.size)
            {
                nv.menu.add(
                    0, i, 0,
                    tempArrayListPharmacyChains[i].name as CharSequence
                )
            }
            if (waitingForUpdate || connectionStage == -1)
            {
                waitingForUpdate = false
                if (currentTravelCompanyID != -1)
                {
//                    recyclerViewExams.adapter = CustomRecyclerAdapterForExams(
//                        tco.getToursNames(currentTravelCompanyID),
//                        tco.getToursCountries(currentTravelCompanyID),
//                        tco.getToursNumbers(currentTravelCompanyID),
//                    )
                    val tempCurrentYear = currentYear
                    val tempArrayListOfYears: ArrayList<String> = ArrayList()
                    tempArrayListOfYears.add("Все годы")
                    tempArrayListOfYears.addAll(tco.getToursYears(currentTravelCompanyID))
                    val adapter: ArrayAdapter<String> = ArrayAdapter(activity,
                        android.R.layout.simple_spinner_item, tempArrayListOfYears)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerYears.adapter = adapter
                    if (tempArrayListOfYears.contains(tempCurrentYear))
                    {
                        spinnerYears.setSelection(tempArrayListOfYears.indexOf(tempCurrentYear + 1))
                    }
                    else
                    {
                        refreshBySpinner()
                    }
                }
            }
            connectionStage = 1
        }

        init {
            thread1 = Thread(Thread1Server())
            thread1!!.start()
            threadT = Thread(ThreadT())
            threadT!!.start()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Snackbar.make(findViewById(R.id.app_bar_main),
            "Выбрана фирма: ${item.title}.",
            Snackbar.LENGTH_LONG)
            .show()
        drawerLayout.closeDrawer(GravityCompat.START)
        textViewForStart.visibility = View.INVISIBLE
        groupTitle = item.title as String
        toolbar.title = groupTitle
        invalidateOptionsMenu()
        currentTravelCompanyID = item.itemId

//        recyclerViewExams.adapter = CustomRecyclerAdapterForExams(
//            tco.getToursNames(currentTravelCompanyID),
//            tco.getToursCountries(currentTravelCompanyID),
//            tco.getToursNumbers(currentTravelCompanyID))
        recyclerViewExams.visibility = View.VISIBLE

        val tempArrayListOfYears: ArrayList<String> = ArrayList()
        tempArrayListOfYears.add("Все годы")
        tempArrayListOfYears.addAll(tco.getToursYears(currentTravelCompanyID))
        val adapter: ArrayAdapter<String> = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, tempArrayListOfYears)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYears.adapter = adapter
        spinnerYears.visibility = View.VISIBLE
        return true
    }

    private fun refreshBySpinner()
    {
        recyclerViewExams.adapter = CustomRecyclerAdapterForExams(
            tco.getToursNames(currentTravelCompanyID),
            tco.getToursCountries(currentTravelCompanyID),
            tco.getToursNumbers(currentTravelCompanyID),
            tco.getAllToursYears(currentTravelCompanyID),
            currentYear
        )
    }

    fun delTour()
    {
        connection.sendDataToServer("d$currentTravelCompanyID,$currentTourID")
        waitingForUpdate = true
    }

    override fun sendInputSortId(sortId: Int)
    {
        if (sortId > -1 && sortId < 8)      // Сортировка
        {
            tco.sortTours(currentTravelCompanyID, sortId)
            if (connectionStage == 1)
            {
                connection.sendDataToServer("u" + gson.toJson(tco))
            }
            toolbar.title = when (sortId)
            {
                0 -> "$groupTitle сорт. Назввние"
                1 -> "$groupTitle сорт. Страна"
                2 -> "$groupTitle сорт. Кол-во"
                3 -> "$groupTitle сорт. Д. Пр."
                4 -> "$groupTitle сорт. Д. Нач."
                5 -> "$groupTitle сорт. Сумма"
                6 -> "$groupTitle сорт. Распродано"
                7 -> "$groupTitle сорт. Описание"
                else -> groupTitle
            }
        }
        if (sortId == 8)        // Удаление
        {
            val manager: FragmentManager = supportFragmentManager
            val myDialogFragmentDelTour = MyDialogFragmentDelTour()
            val bundle = Bundle()
            bundle.putString("name", tco.getTour(currentTravelCompanyID, currentTourID).name)
            myDialogFragmentDelTour.arguments = bundle
            myDialogFragmentDelTour.show(manager, "myDialog")
        }
        if (sortId == 9)        // Изменение
        {
            val tempExam = tco.getTour(currentTravelCompanyID, currentTourID)
            val intent = Intent()
            intent.setClass(this, EditTourActivity::class.java)
            intent.putExtra("action", 2)
            intent.putExtra("name", tempExam.name)
            intent.putExtra("country", tempExam.country)
            intent.putExtra("number", tempExam.num.toString())
            intent.putExtra("dateOfSale", tempExam.dateOfSale)
            intent.putExtra("dateStart", tempExam.dateStart)
            intent.putExtra("sum", tempExam.sum.toString())
            intent.putExtra("isSoldOut", tempExam.isSoldOut.toString())
            intent.putExtra("comment", tempExam.comment)
            resultLauncher.launch(intent)
        }
//        recyclerViewExams.adapter = CustomRecyclerAdapterForExams(
//            tco.getToursNames(currentTravelCompanyID),
//            tco.getToursCountries(currentTravelCompanyID),
//            tco.getToursNumbers(currentTravelCompanyID))
        refreshBySpinner()
    }

    private fun processOnActivityResult(data: Intent?)
    {
        val action = data!!.getIntExtra("action", -1)
        val name = data.getStringExtra("name")
        val address = data.getStringExtra("country")
        val number = data.getIntExtra("number", -1)
        val timeOpen = data.getStringExtra("dateOfSale")
        val timeClose = data.getStringExtra("dateStart")
        val parking = data.getIntExtra("sum", -1)
        val delivery = data.getIntExtra("isSoldOut", 0)
        val comment = data.getStringExtra("comment")
        val tempPharmacy = Tour(name!!, address!!, number, timeOpen!!, timeClose!!, parking
            , delivery, comment!!)
        val tempExamJSON: String = gson.toJson(tempPharmacy)

        if (action == 1)
        {
            val tempStringToSend = "a${tco.getTravelCompanies()[currentTravelCompanyID].name}##$tempExamJSON"
            connection.sendDataToServer(tempStringToSend)
            waitingForUpdate = true
        }
        if (action == 2)
        {
            val tempStringToSend = "e$currentTravelCompanyID,$currentTourID##$tempExamJSON"
            connection.sendDataToServer(tempStringToSend)
            waitingForUpdate = true
        }
        if (action == -1)
        {
            Snackbar.make(findViewById(R.id.app_bar_main),
                "Ошибка добавления/изменения!",
                Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.RED)
                .show()
        }
    }
}