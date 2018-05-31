package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ElementMaybe
import at.cpickl.gadsu.client.YinYangMaybe
import at.cpickl.gadsu.client.xprops.view.GridBagFill
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.titledBorder
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.awt.GridBagConstraints

class ClientTab5e(
        initialClient: Client,
        modificationChecker: ModificationChecker,
        bus: EventBus
) : DefaultClientTab(
        tabTitle = Labels.Tabs.Client5e,
        type = ClientTabType.FIVEE
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val fields = Fields<Client>(modificationChecker)

    val inpYyTendency = fields.newComboBox(YinYangMaybe.Enum.orderedValues, initialClient.yyTendency, "Tendenz", { it.yyTendency }, ViewNames.Client.InputYyTendency)
    val inpTextYinYang = fields.newTextArea("Beschreibung", { it.textYinYang }, ViewNames.Client.InputTextYinYang, bus)

    val inpElementTendency = fields.newComboBox(ElementMaybe.Enum.orderedValues, initialClient.elementTendency, "Tendenz", { it.elementTendency }, ViewNames.Client.InputElementTendency)
    val inpFiveElements = fields.newTextArea("Beschreibung", { it.textFiveElements }, ViewNames.Client.InputTextFiveElements, bus)
    val inpTextWood = fields.newTextArea("Holz", { it.textWood }, ViewNames.Client.InputTextFiveElements + "Wood", bus)
    val inpTextFire = fields.newTextArea("Feuer", { it.textFire }, ViewNames.Client.InputTextFiveElements + "Fire", bus)
    val inpTextEarth = fields.newTextArea("Erde", { it.textEarth }, ViewNames.Client.InputTextFiveElements + "Earth", bus)
    val inpTextMetal = fields.newTextArea("Metall", { it.textMetal }, ViewNames.Client.InputTextFiveElements + "Metal", bus)
    val inpTextWater = fields.newTextArea("Wasser", { it.textWater }, ViewNames.Client.InputTextFiveElements + "Water", bus)

    init {
        val formYy = FormPanel(fillCellsGridy = false).apply {
            titledBorder("Yin Yang")
            addFormInput(label = inpYyTendency.formLabel, input = inpYyTendency.toComponent(), fillType = GridBagFill.None, inputWeighty = 0.0)
            addFormInput(label = inpTextYinYang.formLabel, input = inpTextYinYang.toComponent(), fillType = GridBagFill.Both, inputWeighty = 1.0)
        }

        val form5e = FormPanel(fillCellsGridy = false).apply {
            titledBorder("5 Elemente")
            addFormInput(label = inpElementTendency.formLabel, input = inpElementTendency.toComponent(), fillType = GridBagFill.None, inputWeighty = 0.0)
            addFormInput(label = inpFiveElements.formLabel, input = inpFiveElements.toComponent(), fillType = GridBagFill.Both, inputWeighty = 0.2)
            addFormInput(label = inpTextWood.formLabel, input = inpTextWood.toComponent(), fillType = GridBagFill.Both, inputWeighty = 0.2)
            addFormInput(label = inpTextFire.formLabel, input = inpTextFire.toComponent(), fillType = GridBagFill.Both, inputWeighty = 0.2)
            addFormInput(label = inpTextEarth.formLabel, input = inpTextEarth.toComponent(), fillType = GridBagFill.Both, inputWeighty = 0.2)
            addFormInput(label = inpTextMetal.formLabel, input = inpTextMetal.toComponent(), fillType = GridBagFill.Both, inputWeighty = 0.2)
            addFormInput(label = inpTextWater.formLabel, input = inpTextWater.toComponent(), fillType = GridBagFill.Both, inputWeighty = 0.2)
        }

        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 0.3
        add(formYy)

        c.gridy++
        c.weighty = 0.7
        add(form5e)
    }

    override fun isModified(client: Client) = fields.isAnyModified(client)

    override fun updateFields(client: Client) {
        log.trace("updateFields(client={})", client)
        fields.updateAll(client)
    }

}
