import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UI extends JFrame {
    private static final int CELL_SIZE = 32;
    private static final ImageIcon FLAG = new ImageIcon("flag_28dp.png");
    private static final ImageIcon BOMB = new ImageIcon("bomb_28dp.png");

    private Game game;
    private JPanel panel;
    private JLabel[][] tiles;

    public UI(int rows, int cols, int mines) {
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        game = new Game(rows, cols, mines);

        panel = new JPanel(new GridLayout(rows, cols, 1,1));
        panel.setBackground(new Color(0,0,0));

        tiles = new JLabel[rows][cols];

        for (int y = 0; y< tiles.length; y++) {
            for (int x = 0; x< tiles[0].length; x++) {
                JLabel tile = getNewTile(x, y, rows, cols, mines);
                tiles[y][x] = tile;
                panel.add(tile);
            }
        }

        add(panel);
        pack();
        setLocationRelativeTo(null); // put window in centre
        setVisible(true);
    }

    private JLabel getNewTile(int x, int y, int rows, int cols, int mines) {
        JLabel tile = new JLabel();
        tile.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        tile.setOpaque(true);
        tile.setVerticalTextPosition(SwingConstants.CENTER);
        tile.setHorizontalAlignment(SwingConstants.CENTER);
        tile.setFont(new Font("Sans-Serif", Font.BOLD, 20));

        tile.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!game.isRunning()) {
                    game = new Game(rows, cols, mines);
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    game.dig(x, y);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    game.flag(x, y);
                }
                render(game);
            }
        });

        return tile;
    }

    private void render(Game game) {
        int[][] cells = game.getPlayerCells();

        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[0].length; x++) {
                JLabel tile = tiles[y][x];
                int cell = cells[y][x];

                switch (cell) {
                    case 0 -> {
                        tile.setIcon(null);
                        tile.setBackground(new Color(215, 215, 215));
                    }
                    case 1,2,3,4,5,6,7,8 -> {
                        tile.setIcon(null);
                        tile.setBackground(new Color(215, 215, 215));
                        renderTileNum(tile, cell);
                    }
                    case 9 -> {
                        tile.setIcon(BOMB);
                        tile.setBackground(new Color(238, 238, 238));
                        tile.setText("");
                    }
                    case -1 -> {
                        tile.setIcon(null);
                        tile.setBackground(new Color(238, 238, 238));
                        tile.setText("");
                    }
                    case -2 -> {
                        tile.setIcon(FLAG);
                        tile.setBackground(new Color(238, 238, 238));
                        tile.setText("");
                    }
                }
            }
        }
    }

    private void renderTileNum(JLabel tile, int num) {
        tile.setForeground(switch (num) {
            case 1 -> new Color(41, 98, 255);
            case 2 -> new Color(0, 200, 83);
            case 3 -> new Color(213, 0, 0);
            case 4 -> new Color(98, 0, 234);
            case 5 -> new Color(255, 109, 0);
            case 6 -> new Color(0, 184, 212);
            case 7 -> new Color(170, 0, 255);
            case 8 -> new Color(97, 97, 97);
            default -> new Color(0,0,0);
        });
        tile.setText(Integer.toString(num));
    }
}
