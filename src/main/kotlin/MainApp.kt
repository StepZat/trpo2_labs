import tornadofx.*
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.collections.FXCollections
import javafx.beans.property.SimpleObjectProperty
import javafx.stage.FileChooser
import java.io.*


class MainApp : App(MainView::class)

class MainView : View("Vector HashTable Management") {
    private val hashTable = HashTable<Int, Vector>()
    private val tableView = tableview<Pair<Int, String>> {
        column<Pair<Int, String>, Number>("Bucket Index") {
            SimpleObjectProperty(it.value.first)
        }
        column<Pair<Int, String>, String>("Key and Value") {
            SimpleObjectProperty(it.value.second)
        }
        columnResizePolicy = SmartResize.POLICY
    }


    init {
        hashTable.onRehash = { updateTableView() }
    }

    private val comboBox = combobox<String> {
        items = FXCollections.observableArrayList("Polar Vector", "Cartesian Vector")
        value = "Polar Vector"
    }

    init {
        VectorFactory.registerType("Polar Vector", PolarVectorType())
        VectorFactory.registerType("Cartesian Vector", CartesianVectorType())
    }

    private val keyField = textfield { promptText = "Enter Key" }
    private val valueField1 = textfield { promptText = "Enter Length" }
    private val valueField2 = textfield { promptText = "Enter Angle" }
    private val addButton = button("Add")
    private val deleteButton = button("Delete")
    private val findKeyField = textfield { promptText = "Enter Key to Find" }
    private val findButton = button("Find")
    private val findResultLabel = label("Enter a key and press 'Find' to search.")
    private val exportButton = button("Export to CSV")
    private val importButton = button("Import from CSV")
    private val clearButton = button("Clear")

    override val root = vbox(10) {
        add(label("Hash Table Contents:"))
        add(tableView)
        hbox(10) {
            add(comboBox)
            add(keyField)
            add(valueField1)
            add(valueField2)
            add(addButton)
            add(deleteButton)
            add(clearButton)
        }
        hbox(10) {
            add(findKeyField)
            add(findButton)
            add(findResultLabel)
        }
        hbox(10) {
            add(exportButton)
            add(importButton)
        }
    }


    init {
        comboBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            updatePromptTexts(newValue as String)
        }
        addButton.action {
            addEntry()
            updateTableView()
        }
        deleteButton.action {
            removeEntry()
            updateTableView()
        }
        findButton.action {
            findEntry()
        }
        exportButton.action {
            exportToCSV()
        }
        importButton.action {
            importFromCSV()
        }
        clearButton.action {
            hashTable.clear()
            updateTableView()
        }
    }

    private fun updatePromptTexts(selectedItem: String) {
        if (selectedItem == "Polar Vector") {
            keyField.promptText = "Enter Key"
            valueField1.promptText = "Enter Length"
            valueField2.promptText = "Enter Angle"
            keyField.text = ""
            valueField1.text = ""
            valueField2.text = ""
        } else {
            keyField.promptText = "Enter Key"
            valueField1.promptText = "Enter X Coordinate"
            valueField2.promptText = "Enter Y Coordinate"
            keyField.text = ""
            valueField1.text = ""
            valueField2.text = ""
        }
    }

    private fun addEntry() {
        val keyText = keyField.text
        val value1Text = valueField1.text
        val value2Text = valueField2.text

        if (keyText.isEmpty() || value1Text.isEmpty() || value2Text.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Please fill all fields.")
        } else {
            try {
                val key = keyText.toInt()
                val value1 = value1Text.toDouble()
                val value2 = value2Text.toDouble()

                val vectorType = comboBox.value
                val vector = VectorFactory.createVector(vectorType)
                when (vector) {
                    is PolarVector -> hashTable.put(key, vector.copy(length = value1, angle = value2))
                    is CartesianVector -> hashTable.put(key, vector.copy(x = value1, y = value2))
                }

                updateTableView()
                updatePromptTexts(vectorType)
            } catch (e: NumberFormatException) {
                alert(Alert.AlertType.ERROR, "Invalid input. Please enter valid numbers.")
            }
        }
    }

    private fun removeEntry() {
        val keyText = keyField.text
        try {
            val key = keyText.toInt()
            val removedVector = hashTable.remove(key)
            if (removedVector != null) {
                println("Removed: ${removedVector.keyRepresentation}")
                updateTableView()
            } else {
                alert(Alert.AlertType.INFORMATION, "Element not found.")
            }
        } catch (e: NumberFormatException) {
            alert(Alert.AlertType.ERROR, "Please enter a valid integer as a key.")
        }
    }

    private fun findEntry() {
        val keyText = findKeyField.text
        try {
            val key = keyText.toInt()
            val vector = hashTable.get(key)
            if (vector != null) {
                findResultLabel.text = "Found: ${vector.keyRepresentation}"
            } else {
                findResultLabel.text = "Nothing was found."
            }
        } catch (e: NumberFormatException) {
            findResultLabel.text = "Please enter a valid integer key."
        }
    }

    private fun exportToCSV() {
        val fileChooser = FileChooser()
        fileChooser.title = "Save as CSV"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("CSV Files", "*.csv"))
        val file = fileChooser.showSaveDialog(currentWindow)
        file?.let {
            BufferedWriter(FileWriter(it)).use { bw ->
                hashTable.getBucketData().forEach { (index, nodeList) ->
                    nodeList.forEach { node ->
                        bw.write("${node.key};${node.value.keyRepresentation}\n")
                    }
                }
            }
        }
    }

    private fun importFromCSV() {
        val fileChooser = FileChooser()
        fileChooser.title = "Open CSV File"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("CSV Files", "*.csv"))
        val file = fileChooser.showOpenDialog(currentWindow)
        file?.let {
            BufferedReader(FileReader(it)).useLines { lines ->
                lines.forEach { line ->
                    val (key, value) = line.split(";")
                    val vector = if (value.startsWith("Polar")) {
                        val params = value.removePrefix("Polar: Length=").removeSuffix(")").split(", Angle=")
                        PolarVector(params[0].toDouble(), params[1].toDouble())
                    } else {
                        val params = value.removePrefix("Cartesian: X=").removeSuffix(")").split(", Y=")
                        CartesianVector(params[0].toDouble(), params[1].toDouble())
                    }
                    hashTable.put(key.toInt(), vector)
                }
            }
            updateTableView()
        }
    }

    private fun updateTableView() {
        val data = hashTable.getBucketData()
            .map { (index, nodes) ->
                val content = nodes.joinToString(", ") { node -> "${node.key} -> ${node.value.keyRepresentation}" }
                index to content
            }

        tableView.items = FXCollections.observableArrayList(data)
    }
}

