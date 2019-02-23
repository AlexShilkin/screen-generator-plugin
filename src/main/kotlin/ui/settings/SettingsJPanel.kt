package ui.settings

import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.*
import com.intellij.ui.components.JBList
import com.intellij.ui.layout.LCFlags
import com.intellij.ui.layout.panel
import model.FileType
import model.ScreenElement
import org.jetbrains.kotlin.idea.KotlinLanguage
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.ListSelectionModel

class SettingsJPanel(project: Project) : JPanel() {

    val nameTextField = JTextField()
    val kotlinTemplateEditorTextField = LanguageTextField(KotlinLanguage.INSTANCE, project, "", false)
    val kotlinSampleEditorTextField = LanguageTextField(KotlinLanguage.INSTANCE, project, "", false).apply {
        isEnabled = false
    }
    val xmlTemplateEditorTextField = LanguageTextField(XMLLanguage.INSTANCE, project, "", false).apply {
        isVisible = false
    }
    val xmlSampleEditorTextField = LanguageTextField(XMLLanguage.INSTANCE, project, "", false).apply {
        isEnabled = false
        isVisible = false
    }

    val listModel = CollectionListModel<ScreenElement>()
    val list = JBList<ScreenElement>(listModel).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
    }
    val toolbarDecorator: ToolbarDecorator = ToolbarDecorator.createDecorator(list)

    val activityTextField = JTextField()
    val fragmentTextField = JTextField()

    val fileTypeComboBoxModel = CollectionComboBoxModel<FileType>(FileType.values().toList())
    val fileTypeComboBox = ComboBox<FileType>(fileTypeComboBoxModel)

    init {
        layout = BorderLayout()
    }

    fun create() {
        val toolbarPanel = toolbarDecorator.createPanel()

        val androidComponentsPanel = panel(LCFlags.fillX, title = "Android Components") {
            row("Activity Base Class:") { activityTextField(growX) }
            row("Fragment Base Class:") { fragmentTextField(growX) }
        }

        val screenElementDetailsPanel = panel(LCFlags.fillX, title = "Screen Element Details") {
            row("Screen Element Name:") { nameTextField(growX) }
            row("File Type:") { fileTypeComboBox(growX) }
        }

        val templatePanel = panel(LCFlags.fillX, title = "Template") {
            row { kotlinTemplateEditorTextField(growX, growY, pushY) }
            row { xmlTemplateEditorTextField(growX, growY, pushY) }
        }

        val samplePanel = panel(LCFlags.fillX, title = "Sample Code") {
            row { kotlinSampleEditorTextField(growX, growY, pushY) }
            row { xmlSampleEditorTextField(growX, growY, pushY) }
        }

        val rightPanel = panel(LCFlags.fillX) {
            row { androidComponentsPanel(growX) }
            row { screenElementDetailsPanel(growX) }
            row { templatePanel(growX, growY, pushY) }
            row { samplePanel(growX, growY, pushY) }
        }

        add(JBSplitter(0.3f).apply
        {
            firstComponent = toolbarPanel
            secondComponent = rightPanel
        }, BorderLayout.CENTER)
    }

    fun setXmlTextFieldsVisible(isVisible: Boolean) {
        xmlTemplateEditorTextField.isVisible = isVisible
        xmlSampleEditorTextField.isVisible = isVisible
    }

    fun setKotlinTextFieldsVisible(isVisible: Boolean) {
        kotlinTemplateEditorTextField.isVisible = isVisible
        kotlinSampleEditorTextField.isVisible = isVisible
    }
}