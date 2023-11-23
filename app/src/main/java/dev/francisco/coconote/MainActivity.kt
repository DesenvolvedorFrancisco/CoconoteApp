package dev.francisco.coconote

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {
    var addNoteBtn: FloatingActionButton? = null
    var recyclerView: RecyclerView? = null
    var menuBtn: ImageButton? = null
    var noteAdapter: NoteAdapter? = null
    private var mAdview: AdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) { }
        mAdview = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdview.loadAd(adRequest)
        addNoteBtn = findViewById(R.id.add_note_btn)
        recyclerView = findViewById(R.id.recycle_view)
        menuBtn = findViewById(R.id.menu_btn)
        addNoteBtn.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    this@MainActivity,
                    NoteDetailsActivity::class.java
                )
            )
        })
        menuBtn.setOnClickListener(View.OnClickListener { v: View? -> showMenu() })
        setupRecyclerView()
    }

    fun showMenu() {
        val popupMenu = PopupMenu(this@MainActivity, menuBtn)
        popupMenu.menu.add("\uD83D\uDC64Terminar sessão➡️")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem ->
            if (menuItem.title === "\uD83D\uDC64Terminar sessão➡️") {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                return@OnMenuItemClickListener true
            }
            false
        })
    }

    fun setupRecyclerView() {
        val query = Utility.getCollectionReferenceForNotes()
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java).build()
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter(options, this)
        recyclerView!!.adapter = noteAdapter
    }

    override fun onStart() {
        super.onStart()
        noteAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter!!.stopListening()
    }

    override fun onResume() {
        super.onResume()
        noteAdapter!!.notifyDataSetChanged()
    }
}