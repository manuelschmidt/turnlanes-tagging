package org.openstreetmap.josm.plugins.turnlanestagging.preset.ui.unidirectional;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import static org.openstreetmap.josm.gui.mappaint.mapcss.ExpressionFactory.Functions.tr;
import org.openstreetmap.josm.plugins.turnlanestagging.bean.BLine;
import org.openstreetmap.josm.plugins.turnlanestagging.bean.BRoad;
import org.openstreetmap.josm.plugins.turnlanestagging.preset.ui.PresetsData;
import org.openstreetmap.josm.plugins.turnlanestagging.preset.ui.TurnSelection;

/**
 *
 * @author ruben
 */
public class TurnSelectionUnidirectional extends JPanel {

    JPanel jpanelcontent = null;
    JPanel jpanelcontentTurns = null;

    //Panel for create the number of lines.
    JPanel jpanelcontentSelections = null;
    private JComboBox<Integer> jcbNumLanes = null;
    private final JTextField jtfChangeR = new JTextField();

    // Event Changed
    public static final String LINESCHANGED = "LinesChanged";

    //To avoid automatic changes
    boolean clickLanesAction = true;

    //Road    
    BRoad valBRoad = new BRoad();

    //Preset Data
    PresetsData presetsData = new PresetsData();

    //Constructor
    public TurnSelectionUnidirectional() {
        super();
        init();
    }

    public void init() {
        //add in Main Panel
        setLayout(new BorderLayout());
        add(buildselect(), BorderLayout.NORTH);
        jpanelcontentTurns = new JPanel();
        add(jpanelcontentTurns, BorderLayout.CENTER);
        add(jtfChangeR, BorderLayout.SOUTH);

        //Event Road Listenr
        jtfChangeR.getDocument().addDocumentListener(new SetLinesChangeListener());
    }

    //Build pannel for   lanes
    protected JPanel buildselect() {
        jpanelcontentSelections = new JPanel(new GridLayout(1, 2));
        //fill Combo Box
        jcbNumLanes = new JComboBox<>();
        for (int j = 0; j < 15; j++) {
            jcbNumLanes.addItem(j + 1);
        }
        jpanelcontentSelections.add(new JLabel(tr("Number of lanes")));
        jpanelcontentSelections.add(jcbNumLanes);
        jcbNumLanes.addActionListener(new JcbNumLanesListener());
        return jpanelcontentSelections;
    }

    private class JcbNumLanesListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            int selected = (int) jcbNumLanes.getSelectedItem();
            if (clickLanesAction) {

                lanes(presetsData.defaultData(selected));
            }
            clickLanesAction = true;
        }
    }

    public void lanes(BRoad road) {

        jpanelcontentTurns.removeAll();

        //Clone objtects
        valBRoad.setName(new String(road.getName()));
        List<BLine> listbl = new ArrayList<>();
        for (int k = 0; k < road.getNumLanes(); k++) {
            BLine bl = new BLine(new Integer(road.getListLines().get(k).getPosition()), new String(road.getListLines().get(k).getTurn()));
            listbl.add(bl);
        }
        valBRoad.setListLines(listbl);

        jpanelcontentTurns.setLayout(new GridLayout(1, valBRoad.getNumLanes()));
        int numLanes = valBRoad.getNumLanes();

        clickLanesAction = false;
        jcbNumLanes.setSelectedIndex(numLanes - 1);

        final List<BLine> listBLines = valBRoad.getListLines();
        for (int i = 0; i < numLanes; i++) {
            BLine bLine = listBLines.get(i);
            final TurnSelection turnSelection = new TurnSelection(bLine);
            turnSelection.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(turnSelection.jRBLeft_CHANGED)) {
                        listBLines.add((BLine) evt.getNewValue());
                        valBRoad.setListLines(listBLines);
                        jtfChangeR.setText(valBRoad.getTagturns());

                    } else if (evt.getPropertyName().equals(turnSelection.jRBRight_CHANGED)) {
                        listBLines.add((BLine) evt.getNewValue());
                        valBRoad.setListLines(listBLines);
                        jtfChangeR.setText(valBRoad.getTagturns());

                    } else if (evt.getPropertyName().equals(turnSelection.jCBThrough_CHANGED)) {
                        listBLines.add((BLine) evt.getNewValue());
                        valBRoad.setListLines(listBLines);
                        jtfChangeR.setText(valBRoad.getTagturns());

                    }
                }
            });
            jpanelcontentTurns.add(turnSelection);
        }
        jtfChangeR.setText(valBRoad.getTagturns());

        jpanelcontentTurns.revalidate();
        jpanelcontentTurns.repaint();
    }

    public void setDefault(BRoad bRoad) {
        lanes(bRoad);
    }

    public BRoad getValBRoad() {
        return valBRoad;
    }

    private class SetLinesChangeListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            firePropertyChange(LINESCHANGED, null, getValBRoad());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

    }

}
