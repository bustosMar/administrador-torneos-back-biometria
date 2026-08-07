package com.sistema.torneos.biometria.app.facade;

import com.digitalpersona.onetouch.DPFPFingerIndex;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.ui.swing.DPFPEnrollmentControl;
import com.digitalpersona.onetouch.ui.swing.DPFPEnrollmentEvent;
import com.digitalpersona.onetouch.ui.swing.DPFPEnrollmentListener;
import com.digitalpersona.onetouch.ui.swing.DPFPEnrollmentVetoException;


import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.EnumMap;
import java.util.EnumSet;

public class HuellaEnrollmentDialogFacade extends JDialog {

    private final EnumMap<DPFPFingerIndex, DPFPTemplate> templates;

    public HuellaEnrollmentDialogFacade(
            Frame owner,
            int maxCount,
            EnumMap<DPFPFingerIndex, DPFPTemplate> templates
    ) {
        super(owner, true);

        this.templates = templates;

        setTitle("Captura de huella");
        setResizable(false);

        DPFPEnrollmentControl enrollmentControl =
                new DPFPEnrollmentControl();

        EnumSet<DPFPFingerIndex> fingers =
                EnumSet.noneOf(DPFPFingerIndex.class);

        fingers.addAll(templates.keySet());

        enrollmentControl.setEnrolledFingers(fingers);
        enrollmentControl.setMaxEnrollFingerCount(maxCount);

        enrollmentControl.addEnrollmentListener(
                new DPFPEnrollmentListener() {

                    @Override
                    public void fingerDeleted(DPFPEnrollmentEvent event)
                            throws DPFPEnrollmentVetoException {

                        HuellaEnrollmentDialogFacade.this.templates
                                .remove(event.getFingerIndex());
                    }

                    @Override
                    public void fingerEnrolled(DPFPEnrollmentEvent event)
                            throws DPFPEnrollmentVetoException {

                        HuellaEnrollmentDialogFacade.this.templates.put(
                                event.getFingerIndex(),
                                event.getTemplate()
                        );

                        setVisible(false);
                    }
                }
        );

        JButton closeButton = new JButton("Cerrar");

        closeButton.addActionListener((ActionEvent e) -> {
            setVisible(false);
        });

        JPanel bottom = new JPanel();
        bottom.add(closeButton);

        getContentPane().setLayout(new BorderLayout());
        add(enrollmentControl, BorderLayout.CENTER);
        add(bottom, BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(null);
    }
}