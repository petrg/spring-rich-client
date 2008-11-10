package org.springframework.richclient.widget;

import org.springframework.richclient.command.AbstractCommand;

import javax.swing.*;
import java.awt.*;


/* ButtonSwitcher is a simple widget that puts 2 buttons from the same command (but with
 * different face id's) on a cardlayout.
 *
 * This makes it easy to switch between 2 faces.
 */
public class ButtonSwitcherWidget extends AbstractWidget
{

    private final CardLayout switcher;
    private final JPanel panel;

    public static final String DEFAULT = "default";
    public static final String ALTERNATIVE = "alternative";

    public ButtonSwitcherWidget(AbstractCommand command, String alternativeFaceId)
    {
        AbstractButton defaultButton = command.createButton();
        AbstractButton alternButton = command.createButton(alternativeFaceId);
        this.switcher = new CardLayout();
        this.panel = new JPanel(this.switcher);
        this.panel.add(defaultButton, DEFAULT);
        this.panel.add(alternButton, ALTERNATIVE);
    }

    /** Het paneel met de 2 buttons achter elkaar in cardlayout. */
    public JComponent getComponent()
    {
        return this.panel;
    }

    /** Toont de default button. */
    public void showDefault()
    {
        this.switcher.first(this.panel);
    }

    /** Toont de alternatieve button. */
    public void showAlternative()
    {
        this.switcher.last(this.panel);
    }

    /** Toont de aangegeven button, voor modes:
     * @see #ALTERNATIVE
     * @see #DEFAULT
     */
    public void show(String mode)
    {
        this.switcher.show(this.panel, mode);

    }
}
