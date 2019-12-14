package data.file

import data.repository.SettingsRepository
import data.repository.SourceRootRepository
import model.AndroidComponent
import model.FileType
import model.Settings
import javax.inject.Inject

private const val LAYOUT_DIRECTORY = "layout"

interface FileCreator {

    fun createScreenFiles(packageName: String, screenName: String, androidComponent: AndroidComponent, module: String)
}

class FileCreatorImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val sourceRootRepository: SourceRootRepository
) : FileCreator {

    override fun createScreenFiles(
        packageName: String,
        screenName: String,
        androidComponent: AndroidComponent,
        module: String
    ) {
        val codeSubdirectory = findCodeSubdirectory(packageName, module)
        val resourcesSubdirectory = findResourcesSubdirectory(module)
        if (codeSubdirectory != null) {
            settingsRepository.loadSettings().apply {
                val baseClass = getAndroidComponentBaseClass(androidComponent)
                screenElements.forEach {
                    if (it.fileType == FileType.LAYOUT_XML) {
                        val file = File(
                            it.fileName(screenName, packageName, androidComponent.displayName, baseClass),
                            it.body(screenName, packageName, androidComponent.displayName, baseClass),
                            it.fileType
                        )
                        resourcesSubdirectory.addFile(file)
                    } else {
                        val file = File(
                            it.fileName(screenName, packageName, androidComponent.displayName, baseClass),
                            it.body(screenName, packageName, androidComponent.displayName, baseClass),
                            it.fileType
                        )
                        codeSubdirectory.addFile(file)
                    }
                }
            }
        }
    }

    private fun findCodeSubdirectory(packageName: String, module: String): Directory? =
        sourceRootRepository.findCodeSourceRoot(module)?.run {
            var subdirectory = directory
            packageName.split(".").forEach {
                subdirectory = subdirectory.findSubdirectory(it) ?: subdirectory.createSubdirectory(it)
            }
            return subdirectory
        }

    private fun findResourcesSubdirectory(module: String) =
        sourceRootRepository.findResourcesSourceRoot(module).directory.run {
            findSubdirectory(LAYOUT_DIRECTORY) ?: createSubdirectory(LAYOUT_DIRECTORY)
        }

    private fun Settings.getAndroidComponentBaseClass(androidComponent: AndroidComponent) = when (androidComponent) {
        AndroidComponent.ACTIVITY -> activityBaseClass
        AndroidComponent.FRAGMENT -> fragmentBaseClass
    }
}