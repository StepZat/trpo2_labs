import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.FileChooser
import java.io.{BufferedReader, BufferedWriter, FileReader, FileWriter, PrintWriter}
import scala.io.Source

object MainApp extends JFXApp {
  val hashTable = new HashTable[Int, Vector]()
  hashTable.onRehash = updateTableView  // Установка callback

  val tableView = new TableView[(Int, String)]() {
    columnResizePolicy = TableView.ConstrainedResizePolicy
    columns ++= List(
      new TableColumn[(Int, String), Int]("Bucket Index") {
        cellValueFactory = { cellDataFeatures => ObjectProperty(cellDataFeatures.value._1) }
      },
      new TableColumn[(Int, String), String]("Key and Value") {
        cellValueFactory = { cellDataFeatures => ObjectProperty(cellDataFeatures.value._2) }
      }
    )
  }

  val comboBox = new ComboBox(Seq("Polar Vector", "Cartesian Vector")) {
    value = "Polar Vector"  // Установка значения по умолчанию
  }
  VectorFactory.registerType("Polar Vector", new PolarVectorType)
  VectorFactory.registerType("Cartesian Vector", new CartesianVectorType)
  val keyField = new TextField { promptText = "Enter Key" }
  val valueField1 = new TextField { promptText = "Enter Length" }
  val valueField2 = new TextField { promptText = "Enter Angle" }
  val addButton = new Button("Add")
  val deleteButton = new Button("Delete")
  val findKeyField = new TextField { promptText = "Enter Key to Find" }
  val findButton = new Button("Find")
  val findResultLabel = new Label("Enter a key and press 'Find' to search.")
  val exportButton = new Button("Export to CSV")
  val importButton = new Button("Import from CSV")
  val clearButton = new Button("Clear")

  stage = new PrimaryStage {
    title = "Vector HashTable Management"
    scene = new Scene(new VBox {
      spacing = 10
      children = Seq(
        new Label("Hash Table Contents:"),
        tableView,
        new HBox {
          spacing = 10
          children = Seq(comboBox, keyField, valueField1, valueField2, addButton, deleteButton, clearButton)
        },
        new HBox {
          spacing = 10
          children = Seq(findKeyField, findButton, findResultLabel)
        },
        new HBox {
          spacing = 10
          children = Seq(exportButton, importButton)
        }
      )
    }, 800, 600)
  }

  comboBox.onAction = (_: ActionEvent) => updatePromptTexts()
  addButton.onAction = (_: ActionEvent) => addEntry()
  deleteButton.onAction = (_: ActionEvent) => removeEntry()
  findButton.onAction = (_: ActionEvent) => findEntry()
  exportButton.onAction = (_: ActionEvent) => exportToCSV()
  importButton.onAction = (_: ActionEvent) => importFromCSV()
  clearButton.onAction = (_: ActionEvent) => {
    hashTable.clear()
    updateTableView() // Обновление интерфейса
  }

  def updatePromptTexts(): Unit = {
    val vectorType = comboBox.value.value
    if (vectorType == "Polar Vector") {
      valueField1.promptText = "Enter Length"
      valueField2.promptText = "Enter Angle"
    } else {
      valueField1.promptText = "Enter X Coordinate"
      valueField2.promptText = "Enter Y Coordinate"
    }
  }

  def addEntry(): Unit = {
    val keyText = keyField.text.value
    val value1Text = valueField1.text.value
    val value2Text = valueField2.text.value

    if (keyText.isEmpty || value1Text.isEmpty || value2Text.isEmpty) {
      new Alert(Alert.AlertType.Warning, "Please fill all fields.").showAndWait()
    } else {
      try {
        val key = keyText.toInt
        val value1 = value1Text.toDouble
        val value2 = value2Text.toDouble

        val vectorType = comboBox.value.value
        val vector = VectorFactory.createVector(vectorType)
        vector match {
          case pv: PolarVector => hashTable.put(key, pv.copy(length = value1, angle = value2))
          case cv: CartesianVector => hashTable.put(key, cv.copy(x = value1, y = value2))
        }

        updateTableView()
      } catch {
        case e: NumberFormatException =>
          new Alert(Alert.AlertType.Error, "Invalid input. Please enter valid numbers.").showAndWait()
      }
    }
  }


  def removeEntry(): Unit = {
    val keyOpt = try {
      Some(keyField.text.value.toInt)
    } catch {
      case _: NumberFormatException =>
        new Alert(Alert.AlertType.Error, "Please enter a valid integer as a key.").showAndWait()
        None
    }

    keyOpt.foreach { key =>
      hashTable.remove(key) match {
        case Some(removedVector) =>
          println(s"Removed: ${removedVector.keyRepresentation}")
          updateTableView()
        case None =>
          new Alert(Alert.AlertType.Information, "Element not found.").showAndWait()
      }
    }
  }

  def findEntry(): Unit = {
    val key = try {
      Some(findKeyField.text.value.toInt)
    } catch {
      case _: NumberFormatException =>
        findResultLabel.text = "Please enter a valid integer key."
        None
    }

    key.foreach { k =>
      hashTable.get(k) match {
        case Some(vector) =>
          findResultLabel.text = s"Found: ${vector.keyRepresentation}"
        case None =>
          findResultLabel.text = "Nothing was found."
      }
    }
  }

  def exportToCSV(): Unit = {
    val fileChooser = new FileChooser {
      title = "Save as CSV"
      extensionFilters += new FileChooser.ExtensionFilter("CSV Files", "*.csv")
    }
    val file = fileChooser.showSaveDialog(stage)
    if (file != null) {
      val bw = new BufferedWriter(new FileWriter(file))
      try {
        hashTable.getBucketData.foreach {
          case (index, vectors) => vectors.foreach { vector =>
            bw.write(s"${vector.key};${vector.value.keyRepresentation}\n")
          }
        }
      } finally {
        bw.close()
      }
    }
  }

  def importFromCSV(): Unit = {
    val fileChooser = new FileChooser {
      title = "Open CSV File"
      extensionFilters += new FileChooser.ExtensionFilter("CSV Files", "*.csv")
    }
    val file = fileChooser.showOpenDialog(stage)
    if (file != null) {
      val source = Source.fromFile(file)
      try {
        for (line <- source.getLines) {
          val Array(key, value) = line.split(";")
          val vector = if (value.startsWith("Polar")) {
            val params = value.stripPrefix("Polar: Length=").stripSuffix(")").split(", Angle=")
            PolarVector(params(0).toDouble, params(1).toDouble)
          } else {
            val params = value.stripPrefix("Cartesian: X=").stripSuffix(")").split(", Y=")
            CartesianVector(params(0).toDouble, params(1).toDouble)
          }
          hashTable.put(key.toInt, vector)
        }
      } finally {
        source.close()
      }
      updateTableView()
    }
  }

  def updateTableView(): Unit = {
    val data = hashTable.getBucketData.filter(_._2.nonEmpty).map { case (index, vectors) =>
      val content = vectors.map(node => s"${node.key} -> ${node.value.keyRepresentation}").mkString(", ")
      (index, content)
    }
    tableView.items = ObservableBuffer(data: _*)
  }
  //def updateTableView(): Unit = {
  //  tableView.items = ObservableBuffer(hashTable.getAll: _*)
  //}
}
