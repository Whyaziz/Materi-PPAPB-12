package com.android.pertemuan12

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.android.pertemuan12.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var executorService: ExecutorService
    private var updateId: Int = 0
    private lateinit var nNotesDao: NoteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        nNotesDao = db!!.noteDao()!!

        with(binding){

            btnTambah.setOnClickListener{
                insert(
                    Note(
                        title = txtTitle.text.toString(),
                        description = txtDesc.text.toString()
                    )
                )
                setEmptyField()
            }

            listView.setOnItemClickListener{
                adapterView, _,i, _ ->

                val item = adapterView.adapter.getItem(i) as Note
                updateId = item.id

                txtTitle.setText(item.title)
                txtDesc.setText(item.description)
            }

            btnUbah.setOnClickListener {
                update(Note(
                    id = updateId,
                    title = txtTitle.text.toString(),
                    description = txtDesc.text.toString()
                ))
            }

            listView.onItemLongClickListener =
                AdapterView.OnItemLongClickListener{
                    adapterView, _,i, _ ->

                val item = adapterView.adapter.getItem(i) as Note
                delete(item)

                true
            }
        }
    }

    private fun setEmptyField() {
        with(binding){
            txtTitle.setText("")
            txtDesc.setText("")
        }
    }

    private fun getAllNotes() {

        nNotesDao.allNotes.observe(this){
            notes ->

            val adapter: ArrayAdapter<Note> = ArrayAdapter<Note>(this, R.layout.simple_list_item_1,notes)
            binding.listView.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        getAllNotes()
    }

    private fun insert(note:Note){
        executorService.execute{
            nNotesDao.insert(note)
        }
    }

    private fun update(note:Note){
        executorService.execute{
            nNotesDao.update(note)
        }
    }

    private fun delete(note:Note){
        executorService.execute{
            nNotesDao.delete(note)
        }
    }
}