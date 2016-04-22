package at.cpickl.gadsu.image

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder

class ImageModule : AbstractModule() {
    override fun configure() {

        bind(ClientImageController::class.java).asEagerSingleton()

        install(FactoryModuleBuilder()
                .implement(ImagePicker::class.java, SwingImagePicker::class.java)
                .build(ImagePickerFactory::class.java))

    }
}
