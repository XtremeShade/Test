/**
 * InventoryDropper - Drops items in inventory
 * Version 0.1
 * Author: Zarqes
 */
 

import kotlinx.coroutines.delay
import net.runelite.api.Inventory
import net.runelite.client.input.KeyListener
import net.runelite.client.input.KeyManager
import net.runelite.client.script.Script
import net.runelite.client.script.ScriptDescriptor
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

@ScriptDescriptor(name = "Inventory Dropper", description = "Drops items in inventory")
class InventoryDropper : Script(), ActionListener, KeyListener {

    private lateinit var keyboard: KeyManager
    private var dropKey: Int = KeyEvent.VK_F5
    private var itemToDrop: String = ""

    private lateinit var itemIdField: JTextField
    private lateinit var dropButton: JButton

    override fun onStart() {
        // Create GUI elements
        val frame = JFrame("Inventory Dropper")
        frame.setSize(300, 150)
        frame.isResizable = false
        frame.setLocationRelativeTo(null)

        val panel = JPanel(GridLayout(2, 1))

        val itemIdLabel = JLabel("Item ID to drop:")
        itemIdField = JTextField()
        panel.add(itemIdLabel)
        panel.add(itemIdField)

        dropButton = JButton("Drop (F5)")
        dropButton.addActionListener(this)
        panel.add(dropButton)

        frame.add(panel)
        frame.isVisible = true

        // Set up keyboard input
        keyboard = client.keyboard
        keyboard.registerKeyListener(this)
    }

    override fun onLoop(): Int {
        val inventory = client.getInventory()
        for (item in inventory.items) {
            if (item.id.toString() == itemToDrop || itemToDrop.isEmpty()) {
                inventory.dropItem(item)
                delay(random(500, 800)) // Randomized delay between drops
            }
        }
        return 1000 // Loop delay in milliseconds
    }

    override fun actionPerformed(e: ActionEvent) {
        if (e.source == dropButton) {
            itemToDrop = itemIdField.text.trim()
            if (itemToDrop.isNotEmpty()) {
                dropButton.text = "Drop (${KeyEvent.getKeyText(dropKey)})"
            } else {
                dropButton.text = "Drop (F5)"
            }
        }
    }

    override fun keyTyped(e: KeyEvent?) {}

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == dropKey) {
            itemToDrop = itemIdField.text.trim()
        }
    }

    override fun keyReleased(e: KeyEvent?) {}

    override fun onStop() {
        // Remove GUI elements
        keyboard.unregisterKeyListener(this)
        client.keyboard.sendKey(dropKey, 0)
        JFrame.getFrames()[0].dispose()
    }
}