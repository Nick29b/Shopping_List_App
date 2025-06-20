package com.example.shoppinglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


data class ShoppingItem(val id : Int ,
                        var name : String ,
                        var quantity : Int ,
                        var isEditing : Boolean = false
)



@Composable
fun ShoppingListApp() {

    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialoge by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("")}
    var itemQuantity by remember { mutableStateOf("")}

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    )
    {
        Button(
            onClick = {showDialoge = true},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        {
            Text("Add Item")
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            items(sItems) {
                item ->
                if(item.isEditing){
                    ShoppingItemEditor(item = item, onEditComplete = {
                        editedName , editedQuantity ->
                        sItems = sItems.map { it.copy(isEditing = false) }
                        val editedItem = sItems.find{it.id == item.id}
                        editedItem?.let {
                            it.name = editedName
                            it.quantity = editedQuantity
                        }
                    })
                }
                else{
                    ShoppingListItem(item = item, onEditClick = {
                        sItems = sItems.map { it.copy(isEditing = it.id==item.id) }
                    }, onDeleteClick = {
                        sItems = sItems - item
                    })
                }
            }
        }
    }

    if (showDialoge) {
        AlertDialog(
            onDismissRequest = { showDialoge = false },

            // Irrespective of where the confirmButton is placed(written) , it is always rendered below the text field

            confirmButton = {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = {
                            if(itemName.isNotBlank()){
                                val newItem = ShoppingItem(
                                    id = sItems.size+1,
                                    name = itemName,
                                    quantity = itemQuantity.toInt()
                                )
                                sItems += newItem
                                showDialoge = false
                                itemName = ""
                                itemQuantity = "1"
                            }
                        }
                    ) {
                        Text("Add")
                    }
                    Button(
                        onClick = { showDialoge = false }
                    ) {
                        Text("Cancel")
                    }
                }
            },
            title = { Text("Add Shopping Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        )
    }
}

@Composable
fun ShoppingListItem(
    item : ShoppingItem,
    onDeleteClick : () -> Unit,
    onEditClick : () -> Unit
){
    Row(modifier = Modifier.fillMaxSize()
        .padding(16.dp)
        .border(
            border =  BorderStroke(2.dp, Color(0XFF018786)),
            shape = RoundedCornerShape(20)
        )
        , horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = item.name, modifier = Modifier.padding(8.dp).align(Alignment.CenterVertically))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp).align(Alignment.CenterVertically))
        Row(modifier = Modifier.padding(16.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Item")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Item")
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(item : ShoppingItem, onEditComplete: (String, Int) -> Unit){
    var editName by remember{ mutableStateOf(item.name) }
    var editQuantity by remember{ mutableStateOf(item.quantity.toString()) }
    var isEditing by remember{ mutableStateOf(item.isEditing) }

    Row(modifier =
        Modifier.padding(16.dp)
            .fillMaxWidth()
            .background(color = Color.White),
            horizontalArrangement = Arrangement.SpaceEvenly){
        Column{
            BasicTextField(
                value = editName,
                onValueChange = {editName = it},
                singleLine = true,
                modifier = Modifier.padding(8.dp).wrapContentSize()
            )
            BasicTextField(
                value = editQuantity,
                onValueChange = {editQuantity = it},
                singleLine = true,
                modifier = Modifier.padding(8.dp).wrapContentSize()
            )
        }

        Button(modifier = Modifier.align(Alignment.CenterVertically),
            onClick = {
                isEditing = false
                onEditComplete(editName, editQuantity.toIntOrNull() ?: 1)
            }
        ) {
           Text("Save")
        }
    }
}