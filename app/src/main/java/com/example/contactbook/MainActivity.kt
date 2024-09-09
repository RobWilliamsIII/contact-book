package com.example.contactbook

// Imports
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    // use lateinit to allow initializing a not-null property outside of a constructor
    private lateinit var editName: EditText
    private lateinit var editNumber: EditText
    private lateinit var contacts: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences

    // variable to select contact to be updated
    private var contactToUpdate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create views
        editName = findViewById(R.id.editName)
        editNumber = findViewById(R.id.editNumber)
        contacts = findViewById(R.id.contacts)

        // create file for shared preferences to be stored
        sharedPreferences = getSharedPreferences("Contacts", Context.MODE_PRIVATE)

        // Set click listener for add/update
        findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            if (contactToUpdate != null) {
                updateContact() // Update contact if an existing contact is being modified
            } else {
                addContact() // Add a new contact
            }
        }

        // Show existing contacts when program runs
        showContacts()
    }

    // Function to add a new contact
    private fun addContact() {
        val name = editName.text.toString().replaceFirstChar { it.uppercase() } // Format name
        val number = editNumber.text.toString()

        // Check if name and number are not empty
        if (name.isNotEmpty() && number.isNotEmpty()) {

            // check if typ is number and 10 digits long
            if (number.length == 10 && number.all { it.isDigit() }) {
                val editor = sharedPreferences.edit()
                editor.putString(name, number) // Save the contact
                editor.apply()
                clearFields() // Clear input fields
                showContacts() // Refresh the contacts list
            } else {

                // error message
                Toast.makeText(this, "Must be 10-digit number", Toast.LENGTH_SHORT).show()
            }
        } else {

            // error message
            Toast.makeText(this, "Add contact name and number", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to update an existing contact
    private fun updateContact() {
        val newName = editName.text.toString().replaceFirstChar { it.uppercase() } // Format name
        val newPhone = editNumber.text.toString()

        // Check contact is selected and contact fields are not null
        if (contactToUpdate != null && newName.isNotEmpty() && newPhone.isNotEmpty()) {

            // check of number is 10 digits
            if (newPhone.length == 10) {
                val editor = sharedPreferences.edit()
                editor.remove(contactToUpdate) // Remove the old contact
                editor.putString(newName, newPhone) // Add the updated contact
                editor.apply()
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                clearFields() // Clear input fields
                showContacts() // Refresh the contacts list
                contactToUpdate = null // Reset the update state
            } else {

                // error message
                Toast.makeText(this, "Must be 10-digit number", Toast.LENGTH_SHORT).show()
            }
        } else {

            // error message
            Toast.makeText(this, "Add contact name and number", Toast.LENGTH_SHORT).show()
        }
    }

    // show all sharedPreferences contacts
    private fun showContacts() {

        // Clear contact fields
        contacts.removeAllViews()

        val allContacts = sharedPreferences.all
        if (allContacts.isNotEmpty()) {
            for ((key, value) in allContacts) {

                // show contact_item view for updating selected contact
                val contactView = layoutInflater.inflate(R.layout.contact_item, null)

                val textViewName = contactView.findViewById<TextView>(R.id.textViewName)

                // Show selected contact after update button clicked
                textViewName.text = "$key\n$value\n"

                // delete functionality
                val buttonDelete = contactView.findViewById<Button>(R.id.buttonDelete)
                buttonDelete.setOnClickListener {
                    deleteContact(key) // Delete the contact when clicked
                }

                //  update functionality
                val buttonUpdate = contactView.findViewById<Button>(R.id.buttonUpdate)
                buttonUpdate.setOnClickListener {
                    updateFields(key, value.toString())
                }

                // Add updated contact sharedPreferences
                contacts.addView(contactView)
            }
        }
    }

    // contact fields show selected contact for updating
    private fun updateFields(name: String, number: String) {
        editName.setText(name)
        editNumber.setText(number)
        contactToUpdate = name
    }

    // Function to delete a contact
    private fun deleteContact(name: String) {
        if (sharedPreferences.contains(name)) {
            val editor = sharedPreferences.edit()
            editor.remove(name)
            editor.apply()
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()

            // reload new sharedPreferences contacts
            showContacts()
        } else {
            Toast.makeText(this, "No existing contact", Toast.LENGTH_SHORT).show()
        }
    }

    // clear the contact fields
    private fun clearFields() {
        editName.text.clear()
        editNumber.text.clear()
    }
}
