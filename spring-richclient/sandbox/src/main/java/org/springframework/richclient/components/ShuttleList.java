/**
 *
 */
package org.springframework.richclient.components;

import org.springframework.richclient.layout.TableLayoutBuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

/**
 * Custom panel that presents a "shuttle" list pair. One list is the "source"
 * and the second list holds the "chosen" values from the source list. Buttons
 * between the lists are used to move entries back and forth. By default, only
 * the chosen list is displayed along with an Edit button. Pressing the edit
 * button exposes the source list and the movement buttons.
 * <p>
 * This component essentially provides an alternate UI for a JList. It uses the
 * same type of model and selection list. The selection is rendered as two lists
 * instead of one list with highlighted entries. Those elements in the model
 * that are not selected are shown in the source list and those that are
 * selected are shown in the chosen list.
 * <p>
 * Normal selection model listeners are used to report changes to interested
 * objects.
 * 
 * @author lstreepy
 * @author Benoit Xhenseval (Small modifications for text + icons config)
 * 
 */
public class ShuttleList extends JPanel {

    private JList _helperList = new JList();

    private JList _sourceList = new JList();

    private JLabel _sourceLabel = new JLabel();

    JPanel _sourcePanel = new JPanel(new BorderLayout());

    JPanel _chosenPanel = new JPanel(new BorderLayout());

    private JList _chosenList = new JList();

    private JLabel _chosenLabel = new JLabel();

    private JScrollPane _helperScroller = new JScrollPane(_helperList);

    private JPanel _buttonPanel;

    private JButton _editButton;

    private ListModel dataModel;

    private Comparator _comparator;

    private boolean _panelsShowing = false;

    private static final long serialVersionUID = -6038138479095186130L;

    /**
     * Simple constructor.
     */
    public ShuttleList() {
        buildComponent();
    }

    /**
     * Returns the object that renders the list items.
     * 
     * @return the <code>ListCellRenderer</code>
     * @see #setCellRenderer
     */
    public ListCellRenderer getCellRenderer() {
        return _sourceList.getCellRenderer();
    }

    /**
     * Sets the delegate that's used to paint each cell in the list.
     * <p>
     * The default value of this property is provided by the ListUI delegate,
     * i.e. by the look and feel implementation.
     * <p>
     * 
     * @param cellRenderer the <code>ListCellRenderer</code> that paints list
     *        cells
     * @see #getCellRenderer
     */
    public void setCellRenderer( ListCellRenderer cellRenderer ) {
        // Apply this to both lists
        _sourceList.setCellRenderer(cellRenderer);
        _chosenList.setCellRenderer(cellRenderer);
        _helperList.setCellRenderer(cellRenderer);
    }

    /**
     * Returns the data model.
     * 
     * @return the <code>ListModel</code> that provides the displayed list of
     *         items
     */
    public ListModel getModel() {
        return dataModel;
    }

    /**
     * Sets the model that represents the contents or "value" of the list and
     * clears the list selection.
     * 
     * @param model the <code>ListModel</code> that provides the list of items
     *        for display
     * @exception IllegalArgumentException if <code>model</code> is
     *            <code>null</code>
     */
    public void setModel( ListModel model ) {
        _helperList.setModel(model);

        dataModel = model;
        clearSelection();

        // Once we have a model, we can properly size the two display lists
        // They should be wide enough to hold the widest string in the model.
        // So take the width of the source list since it currently has all the
        // data.
        Dimension d = _helperScroller.getPreferredSize();
        _chosenPanel.setPreferredSize(d);
        _sourcePanel.setPreferredSize(d);
    }

    /**
     * Sets the preferred number of rows in the list that can be displayed
     * without a scrollbar.
     * 
     * @param visibleRowCount an integer specifying the preferred number of
     *        visible rows
     */
    public void setVisibleRowCount( int visibleRowCount ) {
        _sourceList.setVisibleRowCount(visibleRowCount);
        _chosenList.setVisibleRowCount(visibleRowCount);
        _helperList.setVisibleRowCount(visibleRowCount);

        // Ok, since we've haven't set a preferred size on the helper scroller,
        // we can use it's current preferred size for our two control lists.
        Dimension d = _helperScroller.getPreferredSize();
        _chosenPanel.setPreferredSize(d);
        _sourcePanel.setPreferredSize(d);

    }

    /**
     * Set the comparator to use for comparing list elements.
     * 
     * @param comparator to use
     */
    public void setComparator( Comparator comparator ) {
        _comparator = comparator;
    }

    /**
     * Set the icon to use on the edit button. If no icon is specified, then
     * just the label will be used otherwise the text will be a tooltip.
     * 
     * @param editIcon Icon to use on edit button
     */
    public void setEditIcon( Icon editIcon, String text ) {
        if( editIcon != null ) {
            _editButton.setIcon(editIcon);
            if( text != null ) {
                _editButton.setToolTipText(text);
            }
            _editButton.setText("");
        } else {
            _editButton.setIcon(null);
            if( text != null ) {
                _editButton.setText(text);
            }
        }
    }

    /**
     * Add labels on top of the 2 lists. If not present, do not show the labels.
     * 
     * @param chosenLabel
     * @param sourceLabel
     */
    public void setListLabels( String chosenLabel, String sourceLabel ) {
        if( chosenLabel != null ) {
            _chosenLabel.setText(chosenLabel);
            _chosenLabel.setVisible(true);
        } else {
            _chosenLabel.setVisible(false);
        }

        if( sourceLabel != null ) {
            _sourceLabel.setText(sourceLabel);
            _sourceLabel.setVisible(true);
        } else {
            _sourceLabel.setVisible(false);
        }

        Dimension d = _chosenList.getPreferredSize();
        Dimension d1 = _chosenLabel.getPreferredSize();
        Dimension dChosenPanel = _chosenPanel.getPreferredSize();

        dChosenPanel.width = Math.max(d.width, Math.max(dChosenPanel.width, d1.width));
        _chosenPanel.setPreferredSize(dChosenPanel);

        Dimension dSourceList = _sourceList.getPreferredSize();
        Dimension dSource = _sourceLabel.getPreferredSize();
        Dimension dSourcePanel = _sourcePanel.getPreferredSize();
        dSourcePanel.width = Math.max(dSource.width, Math.max(dSourceList.width, dSourcePanel.width));
        _sourcePanel.setPreferredSize(dSourcePanel);

        Dimension fullPanelSize = getPreferredSize();
        fullPanelSize.width = dSourcePanel.width + dChosenPanel.width
                + (_editButton != null ? _editButton.getPreferredSize().width : 0)
                + (_buttonPanel != null ? _buttonPanel.getPreferredSize().width : 0) + 20;
        setPreferredSize(fullPanelSize);
    }

    /**
     * Build our component panel.
     * 
     * @return component
     */
    protected JComponent buildComponent() {
        _helperList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // JPanel buttonPanel = new ControlButtonPanel();
        JPanel buttonPanel = buildButtonPanel();

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        _sourceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        _chosenList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        _editButton = new JButton("Edit...");
        _editButton.setIconTextGap(0);
        _editButton.setMargin(new Insets(2, 4, 2, 4));

        _editButton.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                togglePanels();
            }
        });

        setLayout(gbl);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        _sourcePanel.add(BorderLayout.NORTH, _sourceLabel);
        JScrollPane sourceScroller = new JScrollPane(_sourceList);
        _sourcePanel.add(BorderLayout.CENTER, sourceScroller);
        gbl.setConstraints(_sourcePanel, gbc);

        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;
        gbc.weighty = 1.0;
        gbl.setConstraints(buttonPanel, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        _chosenPanel.add(BorderLayout.NORTH, _chosenLabel);
        JScrollPane chosenScroller = new JScrollPane(_chosenList);
        _chosenPanel.add(BorderLayout.CENTER, chosenScroller);
        gbl.setConstraints(_chosenPanel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 0, 3);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbl.setConstraints(_editButton, gbc);

        add(_editButton);
        add(_sourcePanel);
        add(buttonPanel);
        add(_chosenPanel);

        _buttonPanel.setVisible(false);
        _sourcePanel.setVisible(false);

        return this;
    }

    /**
     * Construct the control button panel.
     * 
     * @return JPanel
     * 
     */
    protected JPanel buildButtonPanel() {
        _buttonPanel = new JPanel();

        JButton leftToRight = new JButton(">");
        JButton allLeftToRight = new JButton(">>");
        JButton rightToLeft = new JButton("<");
        JButton allRightToLeft = new JButton("<<");

        Font smallerFont = leftToRight.getFont().deriveFont(9.0F);
        leftToRight.setFont(smallerFont);
        allLeftToRight.setFont(smallerFont);
        rightToLeft.setFont(smallerFont);
        allRightToLeft.setFont(smallerFont);

        Insets margin = new Insets(2, 4, 2, 4);
        leftToRight.setMargin(margin);
        allLeftToRight.setMargin(margin);
        rightToLeft.setMargin(margin);
        allRightToLeft.setMargin(margin);

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        _buttonPanel.setLayout(gbl);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(leftToRight, gbc);
        gbl.setConstraints(allLeftToRight, gbc);
        gbl.setConstraints(rightToLeft, gbc);
        gbl.setConstraints(allRightToLeft, gbc);

        _buttonPanel.add(leftToRight);
        _buttonPanel.add(allLeftToRight);
        _buttonPanel.add(rightToLeft);
        _buttonPanel.add(allRightToLeft);

        leftToRight.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                moveLeftToRight();
            }
        });
        allLeftToRight.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                moveAllLeftToRight();
            }
        });
        rightToLeft.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                moveRightToLeft();
            }
        });
        allRightToLeft.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                moveAllRightToLeft();
            }
        });

        _buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        // _buttonPanel.setBackground( Color.lightGray );
        return _buttonPanel;
    }

    /**
     * Toggle the panel visibility. This will hide/show the source list and
     * movement buttons.
     */
    protected void togglePanels() {
        _panelsShowing = !_panelsShowing;
        _sourcePanel.setVisible(_panelsShowing);
        _buttonPanel.setVisible(_panelsShowing);
    }

    /**
     * Move the selected items in the source list to the chosen list. I.e., add
     * the items to our selection model.
     */
    protected void moveLeftToRight() {
        // Loop over the selected items and locate them in the data model, Add
        // these to the selection.
        Object[] sourceSelected = _sourceList.getSelectedValues();
        int nSourceSelected = sourceSelected.length;
        int[] currentSelection = _helperList.getSelectedIndices();
        int[] newSelection = new int[currentSelection.length + nSourceSelected];
        System.arraycopy(currentSelection, 0, newSelection, 0, currentSelection.length);
        int destPos = currentSelection.length;

        for( int i = 0; i < sourceSelected.length; i++ ) {
            newSelection[destPos++] = indexOf(sourceSelected[i]);
        }

        _helperList.setSelectedIndices(newSelection);
        update();
    }

    /**
     * Move all the source items to the chosen side. I.e., select all the items.
     */
    protected void moveAllLeftToRight() {
        int sz = dataModel.getSize();
        int[] selected = new int[sz];
        for( int i = 0; i < sz; i++ ) {
            selected[i] = i;
        }
        _helperList.setSelectedIndices(selected);
        update();
    }

    /**
     * Move the selected items in the chosen list to the source list. I.e.,
     * remove them from our selection model.
     */
    protected void moveRightToLeft() {
        Object[] chosenSelectedValues = _chosenList.getSelectedValues();
        int nChosenSelected = chosenSelectedValues.length;
        int[] chosenSelected = new int[nChosenSelected];

        if( nChosenSelected == 0 ) {
            return; // Nothing to move
        }

        // Get our current selection
        int[] currentSelected = _helperList.getSelectedIndices();
        int nCurrentSelected = currentSelected.length;

        // Fill the chosenSelected array with the indices of the selected chosen
        // items
        for( int i = 0; i < nChosenSelected; i++ ) {
            chosenSelected[i] = indexOf(chosenSelectedValues[i]);
        }

        // Construct the new selected indices. Loop through the current list
        // and compare to the head of the chosen list. If not equal, then add
        // to the new list. If equal, skip it and bump the head pointer on the
        // chosen list.

        int newSelection[] = new int[nCurrentSelected - nChosenSelected];
        int newSelPos = 0;
        int chosenPos = 0;

        for( int i = 0; i < nCurrentSelected; i++ ) {
            int currentIdx = currentSelected[i];
            if( chosenPos < nChosenSelected && currentIdx == chosenSelected[chosenPos] ) {
                chosenPos += 1;
            } else {
                newSelection[newSelPos++] = currentIdx;
            }
        }

        // Install the new selection
        _helperList.setSelectedIndices(newSelection);
        update();
    }

    /**
     * Move all the chosen items back to the source side. This simply sets our
     * selection back to empty.
     */
    protected void moveAllRightToLeft() {
        clearSelection();
    }

    /**
     * Get the index of a given object in the underlying data model.
     * 
     * @param o Object to locate
     * @return index of object in model, -1 if not found
     */
    protected int indexOf( final Object o ) {
        final int size = dataModel.getSize();
        for( int i = 0; i < size; i++ ) {
            if( _comparator == null ) {
                if( o.equals(dataModel.getElementAt(i)) ) {
                    return i;
                }
            } else if( _comparator.compare(o, dataModel.getElementAt(i)) == 0 ) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Update the two lists based on the current selection indices.
     */
    protected void update() {
        int sz = dataModel.getSize();
        int[] selected = _helperList.getSelectedIndices();
        ArrayList sourceItems = new ArrayList(sz);
        ArrayList chosenItems = new ArrayList(selected.length);

        // Start with the source items filled from our data model
        for( int i = 0; i < sz; i++ ) {
            sourceItems.add(dataModel.getElementAt(i));
        }

        // Now move the selected items to the chosen list
        for( int i = selected.length - 1; i >= 0; i-- ) {
            chosenItems.add(sourceItems.remove(selected[i]));
        }

        Collections.reverse(chosenItems); // We built it backwards

        // Now install the two new lists
        _sourceList.setListData(sourceItems.toArray());
        _chosenList.setListData(chosenItems.toArray());
    }

    // ========================
    // List Selection handling
    // ========================

    /**
     * Returns the value of the current selection model.
     * 
     * @return the <code>ListSelectionModel</code> that implements list
     *         selections
     */
    public ListSelectionModel getSelectionModel() {
        return _helperList.getSelectionModel();
    }

    /**
     * Adds a listener to the list that's notified each time a change to the
     * selection occurs.
     * 
     * @param listener the <code>ListSelectionListener</code> to add
     */
    public void addListSelectionListener( ListSelectionListener listener ) {
        _helperList.addListSelectionListener(listener);
    }

    /**
     * Removes a listener from the list that's notified each time a change to
     * the selection occurs.
     * 
     * @param listener the <code>ListSelectionListener</code> to remove
     */
    public void removeListSelectionListener( ListSelectionListener listener ) {
        _helperList.removeListSelectionListener(listener);
    }

    /**
     * Clear the selection. This will populate the source list with all the
     * items from the model and empty the chosen list.
     */
    public void clearSelection() {
        _helperList.clearSelection();
        update();
    }

    /**
     * Selects a set of cells.
     * 
     * @param indices an array of the indices of the cells to select
     */
    public void setSelectedIndices( int[] indices ) {
        _helperList.setSelectedIndices(indices);
        update();
    }

    /**
     * Returns an array of the values for the selected cells. The returned
     * values are sorted in increasing index order.
     * 
     * @return the selected values or an empty list if nothing is selected
     */
    public Object[] getSelectedValues() {
        return _helperList.getSelectedValues();
    }

}
