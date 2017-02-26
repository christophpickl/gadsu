package non_test.fotostrecken_generator

data class Project(
        // used to determine the folder name
        val id: String,
        val title: String,
        val date: String,
        val sections: List<Section>
)

data class Section(
        val title: String,
        val images: List<Image>
)


val EMPTY_IMAGE = Image("", "")

data class Image(
        val file: String,
        val caption: String
)
