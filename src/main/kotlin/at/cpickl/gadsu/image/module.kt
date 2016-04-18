package at.cpickl.gadsu.image

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder

class ImageModule : AbstractModule() {
    override fun configure() {
        install(FactoryModuleBuilder()
                .implement(ImagePicker::class.java, SwingImagePicker::class.java)
                .build(ImagePickerFactory::class.java))
    }
}

interface ImagePickerFactory {
    fun create(viewNamePrefix: String): ImagePicker
}
