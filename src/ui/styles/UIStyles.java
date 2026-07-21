package ui.styles;

import java.awt.Color;
import java.awt.Font;

public final class UIStyles {

    private UIStyles() {
        // Evita crear instancias de esta clase.
    }

    /*
     * =========================================================
     * COLORES PRINCIPALES
     * =========================================================
     */
    public static final Color CITAS_ACCENT_LIGHT =
            new Color(
                    229,
                    240,
                    255
            );
    public static final Color PRIMARY =
            new Color(47, 103, 231);

    public static final Color PRIMARY_DARK =
            new Color(34, 74, 170);

    public static final Color PRIMARY_LIGHT =
            new Color(231, 238, 255);

    public static final Color SECONDARY =
            new Color(25, 166, 154);

    public static final Color SECONDARY_LIGHT =
            new Color(224, 247, 244);

    /*
     * =========================================================
     * COLORES DE ESTADO
     * =========================================================
     */

    public static final Color SUCCESS =
            new Color(35, 155, 95);

    public static final Color SUCCESS_LIGHT =
            new Color(224, 246, 234);

    public static final Color WARNING =
            new Color(230, 155, 35);

    public static final Color WARNING_LIGHT =
            new Color(255, 243, 218);

    public static final Color DANGER =
            new Color(210, 72, 72);

    public static final Color DANGER_LIGHT =
            new Color(255, 230, 230);

    public static final Color INFO =
            new Color(65, 135, 210);

    public static final Color INFO_LIGHT =
            new Color(228, 240, 255);

    /*
     * =========================================================
     * COLORES POR MÓDULO
     * =========================================================
     */

    public static final Color DASHBOARD_ACCENT =
            new Color(47, 103, 231);

    public static final Color CITAS_ACCENT =
            new Color(109, 86, 227);

    public static final Color PACIENTES_ACCENT =
            new Color(25, 166, 154);

    public static final Color MEDICOS_ACCENT =
            new Color(65, 135, 210);

    public static final Color HISTORIAL_ACCENT =
            new Color(225, 145, 45);

    public static final Color FACTURACION_ACCENT =
            new Color(35, 155, 95);

    /*
     * =========================================================
     * FONDOS Y TARJETAS
     * =========================================================
     */

    public static final Color BACKGROUND =
            new Color(245, 247, 250);

    public static final Color SOFT_BACKGROUND =
            new Color(246, 248, 252);

    public static final Color CARD =
            Color.WHITE;

    public static final Color CARD_BACKGROUND =
            Color.WHITE;

    public static final Color HOVER_BACKGROUND =
            new Color(237, 242, 252);

    /*
     * =========================================================
     * SIDEBAR
     * =========================================================
     */

    public static final Color SIDEBAR =
            new Color(15, 23, 42);

    public static final Color SIDEBAR_BACKGROUND =
            new Color(15, 27, 51);

    public static final Color SIDEBAR_HOVER =
            new Color(27, 49, 88);

    public static final Color SIDEBAR_ACTIVE =
            new Color(47, 103, 231);

    public static final Color SIDEBAR_SELECTED =
            new Color(47, 103, 231);

    public static final Color SIDEBAR_TEXT =
            new Color(226, 232, 240);

    /*
     * =========================================================
     * TEXTO Y BORDES
     * =========================================================
     */

    public static final Color TEXT =
            new Color(45, 55, 72);

    public static final Color TEXT_SECONDARY =
            new Color(107, 114, 128);

    public static final Color TEXT_MUTED =
            new Color(145, 153, 166);

    public static final Color BORDER =
            new Color(225, 229, 235);

    public static final Color BORDER_DARK =
            new Color(205, 211, 220);

    /*
     * =========================================================
     * COLORES PARA GRÁFICOS
     * =========================================================
     */

    public static final Color CHART_BLUE =
            new Color(53, 103, 246);

    public static final Color CHART_BLUE_LIGHT =
            new Color(224, 232, 255);

    public static final Color CHART_ORANGE =
            new Color(240, 166, 48);

    public static final Color CHART_ORANGE_LIGHT =
            new Color(255, 239, 210);

    public static final Color CHART_GREEN =
            new Color(35, 155, 95);

    public static final Color CHART_GREEN_LIGHT =
            new Color(224, 246, 234);

    public static final Color CHART_PURPLE =
            new Color(109, 86, 227);

    public static final Color CHART_PURPLE_LIGHT =
            new Color(237, 233, 255);

    /*
     * =========================================================
     * FUENTES
     * =========================================================
     */

    public static final Font TITLE =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    28
            );

    public static final Font SUBTITLE =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    20
            );

    public static final Font SECTION_TITLE =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    17
            );

    public static final Font NORMAL =
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    15
            );

    public static final Font SMALL =
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    13
            );

    public static final Font SMALL_BOLD =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    13
            );

    public static final Font BUTTON =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    15
            );

    public static final Font STAT_VALUE =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    25
            );

    public static final Font SIDEBAR_BUTTON =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    14
            );
}


