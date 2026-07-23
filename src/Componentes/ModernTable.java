package Componentes;

import ui.styles.UIStyles;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class ModernTable extends JTable {



    public ModernTable() {
        super();
        configurarTabla();
    }

    public ModernTable(
            TableModel modelo
    ) {
        super(modelo);
        configurarTabla();
    }



    private void configurarTabla() {
        setFont(
                UIStyles.NORMAL
        );

        setForeground(
                UIStyles.TEXT
        );

        setBackground(
                UIStyles.CARD_BACKGROUND
        );

        setSelectionBackground(
                UIStyles.PRIMARY_LIGHT
        );

        setSelectionForeground(
                UIStyles.PRIMARY_DARK
        );

        setRowHeight(
                44
        );

        setShowVerticalLines(
                false
        );

        setShowHorizontalLines(
                true
        );

        setGridColor(
                UIStyles.BORDER
        );

        setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        setFillsViewportHeight(
                true
        );

        setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        setAutoCreateRowSorter(
                true
        );

        setRowSelectionAllowed(
                true
        );

        setColumnSelectionAllowed(
                false
        );

        setFocusable(
                true
        );

        configurarEncabezado();
    }



    private void configurarEncabezado() {
        JTableHeader encabezado =
                getTableHeader();

        if (encabezado == null) {
            return;
        }

        encabezado.setFont(
                UIStyles.SMALL_BOLD
        );

        encabezado.setForeground(
                UIStyles.TEXT
        );

        encabezado.setBackground(
                UIStyles.SOFT_BACKGROUND
        );

        encabezado.setOpaque(
                true
        );

        encabezado.setReorderingAllowed(
                false
        );

        encabezado.setPreferredSize(
                new Dimension(
                        0,
                        44
                )
        );

        encabezado.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        UIStyles.BORDER
                )
        );

        encabezado.setDefaultRenderer(
                crearRenderizadorEncabezado()
        );
    }

    private TableCellRenderer crearRenderizadorEncabezado() {
        return new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable tabla,
                    Object valor,
                    boolean seleccionado,
                    boolean tieneFoco,
                    int fila,
                    int columna
            ) {
                JLabel etiqueta =
                        (JLabel) super.getTableCellRendererComponent(
                                tabla,
                                valor,
                                seleccionado,
                                tieneFoco,
                                fila,
                                columna
                        );

                etiqueta.setOpaque(
                        true
                );

                etiqueta.setBackground(
                        UIStyles.SOFT_BACKGROUND
                );

                etiqueta.setForeground(
                        UIStyles.TEXT
                );

                etiqueta.setFont(
                        UIStyles.SMALL_BOLD
                );

                etiqueta.setHorizontalAlignment(
                        obtenerAlineacionEncabezado(
                                columna
                        )
                );

                etiqueta.setBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(
                                        0,
                                        0,
                                        1,
                                        0,
                                        UIStyles.BORDER
                                ),
                                BorderFactory.createEmptyBorder(
                                        0,
                                        12,
                                        0,
                                        12
                                )
                        )
                );

                return etiqueta;
            }
        };
    }

    private int obtenerAlineacionEncabezado(
            int columna
    ) {
        if (columna == 0) {
            return SwingConstants.CENTER;
        }

        return SwingConstants.LEFT;
    }



    @Override
    public Component prepareRenderer(
            TableCellRenderer renderer,
            int fila,
            int columna
    ) {
        Component componente =
                super.prepareRenderer(
                        renderer,
                        fila,
                        columna
                );

        boolean seleccionado =
                isCellSelected(
                        fila,
                        columna
                );

        if (seleccionado) {
            componente.setBackground(
                    getSelectionBackground()
            );

            componente.setForeground(
                    getSelectionForeground()
            );
        } else {
            componente.setBackground(
                    fila % 2 == 0
                            ? UIStyles.CARD_BACKGROUND
                            : UIStyles.SOFT_BACKGROUND
            );

            componente.setForeground(
                    UIStyles.TEXT
            );
        }

        if (componente instanceof JComponent) {
            JComponent componenteSwing =
                    (JComponent) componente;

            componenteSwing.setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            12,
                            0,
                            12
                    )
            );

            componenteSwing.setOpaque(
                    true
            );
        }

        return componente;
    }

    @Override
    public void setModel(
            TableModel modelo
    ) {
        super.setModel(
                modelo
        );

        if (getTableHeader() != null) {
            configurarEncabezado();
        }
    }



    public void establecerAlturaFilas(
            int altura
    ) {
        setRowHeight(
                Math.max(
                        24,
                        altura
                )
        );
    }

    public void establecerSeleccionMultiple(
            boolean seleccionMultiple
    ) {
        setSelectionMode(
                seleccionMultiple
                        ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
                        : ListSelectionModel.SINGLE_SELECTION
        );
    }
}