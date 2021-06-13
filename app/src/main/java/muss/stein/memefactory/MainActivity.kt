package muss.stein.memefactory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = navHostFragment!!.findNavController()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.memeTemplateFragment->{
                    navController.popBackStack(R.id.memeTemplateFragment,true)
                    navController.navigate(R.id.memeTemplateFragment)
                    drawerLayout.close()
                }
                R.id.photosSavedToInternalStorage->{
                    navController.popBackStack(R.id.photosSavedToInternalStorage,true)
                    navController.navigate(R.id.photosSavedToInternalStorage)
                    drawerLayout.close()
                }
            }
            true
        }
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.memeTemplateFragment,
                R.id.photosSavedToInternalStorage
            ),
            drawerLayout//what puts the icon...
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}