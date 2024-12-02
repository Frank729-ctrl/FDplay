package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialContacts = mutableStateListOf(
            Contact("John Doe", "123-456-7890"),
            Contact("Jane Smith", "987-654-3210")
        )

        setContent {
            ContactApp(
                onSaveContact = { name, phone ->
                    saveContact(name, phone)
                    initialContacts.add(Contact(name, phone)) // Add new contact to the list
                },
                contacts = initialContacts // Pass the mutable list to update UI dynamically
            )
        }
    }

    private fun saveContact(name: String, phone: String) {
        Toast.makeText(this, "Contact saved: $name - $phone", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ContactApp(
    onSaveContact: (String, String) -> Unit,
    contacts: List<Contact>
) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Gray.copy(alpha = 0.2f))
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name", color = Color.Green) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 7.dp),
            textStyle = TextStyle(color = Color.White),
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number", color = Color.Green) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 7.dp),
            textStyle = TextStyle(color = Color.White),
        )

        Button(
            onClick = {
                if (name.text.isNotEmpty() && phone.text.isNotEmpty()) {
                    onSaveContact(name.text, phone.text)
                    name = TextFieldValue("")
                    phone = TextFieldValue("")
                } else {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Save Contact", color = Color.Green)
        }

        Text(
            text = "Contacts",
            style = MaterialTheme.typography.h6.copy(color = Color.Green),
            modifier = Modifier.padding(bottom = 8.dp)


        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(contacts) { contact ->
                ContactCard(contact = contact)
            }
        }
    }
}

@Composable
fun ContactCard(contact: Contact) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp,
        backgroundColor = Color.Gray.copy(alpha = 0.1f) // Set background here
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.subtitle1.copy(color = Color.Green)
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.body2.copy(color = Color.Green)
                )
            }
        }
    }
}


data class Contact(val name: String, val phoneNumber: String)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewContactApp() {
    ContactApp(
        onSaveContact = { _, _ -> },
        contacts = listOf(
            Contact("John Doe", "0597652642"),
            Contact("Jane Smith", "0206286541")
        )
    )
}
