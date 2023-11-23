package dev.francisco.coconote

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import dev.francisco.coconote.Utility.collectionReferenceForNotes
import dev.francisco.coconote.Utility.showToast

class NoteDetailsActivity : AppCompatActivity() {
    var titleEditText: EditText? = null
    var contentEditText: EditText? = null
    var saveNoteButton: ImageButton? = null
    var pageTitleTextView: TextView? = null
    var title: String? = null
    var content: String? = null
    var docId: String? = null
    var isEditMode = false
    var deleteNoteTextViewBtn: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        titleEditText = findViewById(R.id.notes_title_text)
        contentEditText = findViewById(R.id.notes_content_text)
        saveNoteButton = findViewById(R.id.save_note_btn)
        pageTitleTextView = findViewById(R.id.page_title)
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn)

        // Receber dados
        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        docId = intent.getStringExtra("docId")
        if (docId != null && !docId!!.isEmpty()) {
            isEditMode = true
        }
        titleEditText.setText(title)
        contentEditText.setText(content)
        if (isEditMode) {
            pageTitleTextView.setText("Editar a sua nota")
            deleteNoteTextViewButton.setVisibility(View.VISIBLE)
        }
        saveNoteButton.setOnClickListener(View.OnClickListener { v: View? -> saveNote() })
        deleteNoteTextViewBtn.setOnClickListener(View.OnClickListener { v: View? -> deleteNoteFromFirebase() })
    }

    fun saveNote() {
        val noteTitle = titleEditText!!.text.toString()
        val noteContent = contentEditText!!.text.toString()
        if (noteTitle == null || noteTitle.isEmpty()) {
            titleEditText!!.error = "Precisas dar um titulo"
            return
        }
        val note = Note()
        note.title = noteTitle
        note.content = noteContent
        note.timestamp = Timestamp.now()
        saveNoteToFirebase(note)
    }

    fun saveNoteToFirebase(note: Note?) {
        val documentReference: DocumentReference
        documentReference = if (isEditMode) {
            //actualiza a nota
            collectionReferenceForNotes.document(docId!!)
        } else {
            // cria uma nova nota
            collectionReferenceForNotes.document()
        }
        documentReference.set(note!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // nota foi adicionada
                showToast(this@NoteDetailsActivity, "Nota adicionada com Sucesso!")
                finish()
            } else {
                showToast(this@NoteDetailsActivity, "Falha ao adicionar a Nota!")
            }
        }
    }

    fun deleteNoteFromFirebase() {
        val documentReference: DocumentReference
        documentReference = collectionReferenceForNotes.document(
            docId!!
        )
        documentReference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // nota foi deletada
                showToast(this@NoteDetailsActivity, "Nota eliminada com Sucesso!")
                finish()
            } else {
                showToast(this@NoteDetailsActivity, "Falha ao eliminar a Nota!")
            }
        }
    }
}