import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main {
    public static void main(String[] args) throws AWTException {
        new Dama();
    }
}

class Dama extends JFrame {
    JPanel panel;
    
    Dama() throws AWTException {
        panel = new panel();
        add(panel);
        setResizable(false);
        pack();
        
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}

class panel extends JPanel {
    /* because of the amount of time we had as we're at the doorstep of the final exams, I couldn't manage to add all the
     features, although I made preparations, so adding them later would be easier, the method called isNotOccupied is
     specifically created to add the scoring system although mostly useless now, but it will come in handy later, thanks.
     */
    public static int width = 60;//Resizes EVERYTHING dynamically
    public static int factor = 8;//Changes number of stones and number of cells dynamically
    public static float[] dashPattern = {20, 20, 20};//Dash pattern lengths, works when boolean above is true
    public static int lineThickness = 1;//adjust dashed line thicknesses to your liking
    static boolean dashedLines = false;//Turn dashed lines on and off
    Robot robot;//Used to get color under the cursor
    int closestRedIndex;
    int closestBlueIndex;
    int[] closestCellIndexes;
    boolean firstTimeRun;
    boolean redsTurn = true;
    boolean isSelected;
    Point[] possibleMoves;
    Point[] redCoordinates = new Point[factor * 2];
    Point[] blueCoordinates = new Point[factor * 2];
    Point[][] cellCoordinates = new Point[factor][factor];
    
    panel() throws AWTException {
        firstTimeRun = true;
        robot = new Robot();
        setPreferredSize(new Dimension(width * (factor + 2), width * (factor + 2)));
        setBackground(new Color(238, 238, 238));
        
        Color boardColor = new Color(238, 238, 238);
        Color boarderColor = new Color(51, 51, 51);
        Color alfaGreen = new Color(119, 247, 119);
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                
                PointerInfo info = MouseInfo.getPointerInfo();
                Color clickedColor = robot.getPixelColor(info.getLocation().x, info.getLocation().y);
                int redColor = Color.red.getRed();
                int blueColor = Color.blue.getBlue();
                //If reds turn:
                boolean isCellEmptyByColor = clickedColor.equals(boardColor) || clickedColor.equals(alfaGreen);
                if ((clickedColor.getRed() == redColor || redsTurn) && clickedColor.getBlue() != blueColor) {
                    //Selecting the stone
                    if (!isSelected && redsTurn && !clickedColor.equals(boardColor) && !clickedColor.equals(boarderColor)) {
                        closestRedIndex = closestStoneIndex(e.getPoint(), redCoordinates);
                        closestCellIndexes = closestCellIndexes(redCoordinates[closestRedIndex], cellCoordinates);
                        if (closestCellIndexes[1] != 0 && closestCellIndexes[1] != factor - 1) {
                            possibleMoves = new Point[3];
                            possibleMoves[0] = cellCoordinates[closestCellIndexes[0] + 1][closestCellIndexes[1]];
                            possibleMoves[1] = cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] + 1];
                            possibleMoves[2] = cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] - 1];
                        }
                        else if (closestCellIndexes[1] == 0) {
                            possibleMoves = new Point[2];
                            possibleMoves[0] = cellCoordinates[closestCellIndexes[0] + 1][closestCellIndexes[1]];
                            possibleMoves[1] = cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] + 1];
                        }
                        else {
                            possibleMoves = new Point[2];
                            possibleMoves[0] = cellCoordinates[closestCellIndexes[0] + 1][closestCellIndexes[1]];
                            possibleMoves[1] = cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] - 1];
                        }
                        isSelected = true;
                        repaint();
                    }
                    //Moving the stone
                    else if (isSelected && redsTurn && isCellEmptyByColor && !clickedColor.equals(boarderColor)) {
                        int[] closestIndexesToYourClick = closestCellIndexes(e.getPoint(), cellCoordinates);
                        Point closestPointToYourClick = cellCoordinates[closestIndexesToYourClick[0]][closestIndexesToYourClick[1]];
                        boolean isNotOccupiedByYourStones = isNotOccupied(closestPointToYourClick, redCoordinates);
                        if (closestCellIndexes[1] != 0 && closestCellIndexes[1] != factor - 1) {
                            if ((closestPointToYourClick == cellCoordinates[closestCellIndexes[0] + 1][closestCellIndexes[1]] || closestPointToYourClick == cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] + 1] || closestPointToYourClick == cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] - 1]) && isNotOccupiedByYourStones) {
                                redCoordinates[closestRedIndex] = closestPointToYourClick;
                                redsTurn = false;
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "You Can't: \n- Go Backwards \n- Make Jumps Larger Than One \n- Go Where Your Stones Are", "Warning", JOptionPane.WARNING_MESSAGE);
                                redsTurn = true;
                            }
                        }
                        else if (closestCellIndexes[1] == 0) {
                            if ((closestPointToYourClick == cellCoordinates[closestCellIndexes[0] + 1][closestCellIndexes[1]] || closestPointToYourClick == cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] + 1]) && isNotOccupiedByYourStones) {
                                redCoordinates[closestRedIndex] = closestPointToYourClick;
                                redsTurn = false;
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "You Can't: \n- Go Backwards \n- Go To The Left \n- Make Jumps Larger Than One \n- Go Where Your Stones Are", "Warning", JOptionPane.WARNING_MESSAGE);
                                redsTurn = true;
                            }
                        }
                        else {
                            if ((closestPointToYourClick == cellCoordinates[closestCellIndexes[0] + 1][closestCellIndexes[1]] || closestPointToYourClick == cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] - 1]) && isNotOccupiedByYourStones) {
                                redCoordinates[closestRedIndex] = closestPointToYourClick;
                                redsTurn = false;
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "You Can't: \n- Go Backwards \n- Go To The Right \n- Make Jumps Larger Than One \n- Go Where Your Stones Are", "Warning", JOptionPane.WARNING_MESSAGE);
                                redsTurn = true;
                            }
                        }
                        isSelected = false;
                    }
                }
                //If blues turn
                else if ((clickedColor.getBlue() == blueColor || !redsTurn) && clickedColor.getRed() != redColor) {
                    //Selecting the stone
                    if (!isSelected && !redsTurn && !clickedColor.equals(boardColor) && !clickedColor.equals(boarderColor)) {
                        closestBlueIndex = closestStoneIndex(e.getPoint(), blueCoordinates);
                        closestCellIndexes = closestCellIndexes(blueCoordinates[closestBlueIndex], cellCoordinates);
                        if (closestCellIndexes[1] != 0 && closestCellIndexes[1] != factor - 1) {
                            possibleMoves = new Point[3];
                            possibleMoves[0] = cellCoordinates[closestCellIndexes[0] - 1][closestCellIndexes[1]];
                            possibleMoves[1] = cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] + 1];
                            possibleMoves[2] = cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] - 1];
                        }
                        else if (closestCellIndexes[1] == 0) {
                            possibleMoves = new Point[2];
                            possibleMoves[0] = cellCoordinates[closestCellIndexes[0] - 1][closestCellIndexes[1]];
                            possibleMoves[1] = cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] + 1];
                        }
                        else {
                            possibleMoves = new Point[2];
                            possibleMoves[0] = cellCoordinates[closestCellIndexes[0] - 1][closestCellIndexes[1]];
                            possibleMoves[1] = cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] - 1];
                        }
                        isSelected = true;
                        repaint();
                    }
                    //Moving the stone
                    else if (isSelected && !redsTurn && isCellEmptyByColor && !clickedColor.equals(boarderColor)) {
                        int[] closestIndexesToClick = closestCellIndexes(e.getPoint(), cellCoordinates);
                        Point closestPointToYourClick = cellCoordinates[closestIndexesToClick[0]][closestIndexesToClick[1]];
                        boolean isNotOccupiedByYourStones = isNotOccupied(closestPointToYourClick, blueCoordinates);
                        if (closestCellIndexes[1] != 0 && closestCellIndexes[1] != factor - 1) {
                            if ((closestPointToYourClick == cellCoordinates[closestCellIndexes[0] - 1][closestCellIndexes[1]] || closestPointToYourClick == cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] + 1] || closestPointToYourClick == cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] - 1]) && isNotOccupiedByYourStones) {
                                blueCoordinates[closestBlueIndex] = closestPointToYourClick;
                                redsTurn = true;
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "You Can't: \n- Go Backwards \n- Make Jumps Larger Than One \n- Go Where Your Stones Are", "Warning", JOptionPane.WARNING_MESSAGE);
                                redsTurn = false;
                            }
                        }
                        else if (closestCellIndexes[1] == 0) {
                            if ((closestPointToYourClick == cellCoordinates[closestCellIndexes[0] - 1][closestCellIndexes[1]] || closestPointToYourClick == cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] + 1]) && isNotOccupiedByYourStones) {
                                blueCoordinates[closestBlueIndex] = closestPointToYourClick;
                                redsTurn = true;
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "You Can't: \n- Go Backwards \n- Go To The Left \n- Make Jumps Larger Than One \n- Go Where Your Stones Are", "Warning", JOptionPane.WARNING_MESSAGE);
                                redsTurn = false;
                            }
                        }
                        else {
                            if ((closestPointToYourClick == cellCoordinates[closestCellIndexes[0] - 1][closestCellIndexes[1]] || closestPointToYourClick == cellCoordinates[closestCellIndexes[0]][closestCellIndexes[1] - 1]) && isNotOccupiedByYourStones) {
                                blueCoordinates[closestBlueIndex] = closestPointToYourClick;
                                redsTurn = true;
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "You Can't: \n- Go Backwards \n- Go To The Right \n- Make Jumps Larger Than One \n- Go Where Your Stones Are", "Warning", JOptionPane.WARNING_MESSAGE);
                                redsTurn = false;
                            }
                        }
                        isSelected = false;
                    }
                }
                //Repaint after each click to refresh the graphics
                repaint();
            }
        });
    }
    
    //Find the index of the closest stone to where you clicked
    int closestStoneIndex(Point currentCoordinate, Point[] stoneCoordinates) {
        int index = 0;
        double distance = currentCoordinate.distanceSq(stoneCoordinates[0]);
        for (int i = 1; i < stoneCoordinates.length; i++) {
            if (currentCoordinate.distanceSq(stoneCoordinates[i]) < distance) {
                index = i;
                distance = currentCoordinate.distanceSq(stoneCoordinates[i]);
            }
        }
        return index;
    }
    
    //Find the indexes of the closest empty cell from where you clicked
    int[] closestCellIndexes(Point currentCoordinate, Point[][] cellCoordinates) {
        int[] indexes = new int[2];
        double distance = currentCoordinate.distanceSq(cellCoordinates[0][0]);
        for (int i = 0; i < cellCoordinates.length; i++) {
            for (int j = 0; j < cellCoordinates[i].length; j++) {
                if (currentCoordinate.distanceSq(cellCoordinates[i][j]) < distance) {
                    indexes[0] = i;
                    indexes[1] = j;
                    distance = currentCoordinate.distanceSq(cellCoordinates[i][j]);
                }
            }
        }
        return indexes;
    }
    
    //Find whether the cell you clicked is occupied by a stones or not, can be used for the scoring system later
    boolean isNotOccupied(Point currentCoordinates, Point[] yourStoneCoordinates) {
        for (Point yourStoneCoordinate : yourStoneCoordinates) {
            if (yourStoneCoordinate.distanceSq(currentCoordinates) == 0.0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Graphics2D is only used for the purposes of anti-aliasing so lines become smoother in smaller sizes
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (dashedLines) {
            g2d.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, dashPattern, 10));
        }
        int row = 0;
        int column = 0;
        //Draw Board
        g2d.setColor(new Color(51, 51, 51));
        for (int i = width; i < width * (factor + 2); i += width) {
            try {
                g2d.drawLine(i, width, i, new panel().getPreferredSize().height - width);
            }
            catch (AWTException e) {
                throw new RuntimeException(e);
            }
            try {
                g2d.drawLine(width, i, new panel().getPreferredSize().width - width, i);
            }
            catch (AWTException e) {
                throw new RuntimeException(e);
            }
        }
        
        //Draw Pieces the first time
        if (firstTimeRun) {
            int cellsColumns = 0;
            for (int i = width; i < (width * (factor + 2)) - width; i += width) {
                for (int j = width; j < (width * (factor + 2)) - width; j += width) {
                    cellCoordinates[row][cellsColumns] = new Point(j + (width / 2), i + (width / 2));
                    cellsColumns++;
                    
                    if (row == (factor - (factor - 1)) || row == (factor - (factor - 2))) {
                        redCoordinates[column] = new Point(j + (width / 2), i + (width / 2));
                        g2d.setColor(Color.red);
                        g2d.fillOval((int) redCoordinates[column].getX() - ((width / 4) + Math.round(width / 7)), (int) redCoordinates[column].getY() - ((width / 4) + Math.round(width / 7)), width - (width / 4), width - (width / 4));
                    }
                    
                    if (row == factor - 3 || row == factor - 2) {
                        blueCoordinates[column] = new Point(j + (width / 2), i + (width / 2));
                        g2d.setColor(Color.blue);
                        g2d.fillOval((int) blueCoordinates[column].getX() - ((width / 4) + Math.round(width / 7)), (int) blueCoordinates[column].getY() - ((width / 4) + Math.round(width / 7)), width - (width / 4), width - (width / 4));
                    }
                    if (cellsColumns == factor) {
                        cellsColumns = 0;
                    }
                    column++;
                    if (column == (factor * 2)) {
                        column = 0;
                    }
                }
                row++;
            }
            firstTimeRun = false;
        }
        //Draw Pieces after each move
        for (int i = 0; i < redCoordinates.length; i++) {
            g2d.setColor(Color.red);
            g2d.fillOval((int) redCoordinates[i].getX() - ((width / 4) + Math.round(width / 7)), (int) redCoordinates[i].getY() - ((width / 4) + Math.round(width / 7)), width - (width / 4), width - (width / 4));
            
            g2d.setColor(Color.blue);
            g2d.fillOval((int) blueCoordinates[i].getX() - ((width / 4) + Math.round(width / 7)), (int) blueCoordinates[i].getY() - ((width / 4) + Math.round(width / 7)), width - (width / 4), width - (width / 4));
        }
        //Draw player turn indicators
        if (redsTurn && !isSelected) {
            g2d.setColor(Color.red);
            redPlayerTurnIndicator(g2d);
        } //red
        else if (redsTurn) {
            g2d.setColor(Color.green);
            redPlayerTurnIndicator(g2d);
        }
        if (!redsTurn && !isSelected) {
            g2d.setColor(Color.blue);
            bluePlayerTurnIndicator(g2d);
        } //blue
        else if (!redsTurn) {
            g2d.setColor(Color.green);
            bluePlayerTurnIndicator(g2d);
        }
        
        //Draw Rectangles where moves are possible
        if (isSelected) {
            g2d.setColor(new Color(0f, 1f, 0f, .5f));
            for (Point possibleMove : possibleMoves) {
                if (isNotOccupied(possibleMove, redCoordinates) && isNotOccupied(possibleMove, blueCoordinates)) {
                    if (possibleMove != null) {
                        g2d.fillRect((int) possibleMove.getX() - ((width / 2)), (int) possibleMove.getY() - (width / 2), width, width);
                    }
                }
            }
        }
    }
    
    private void bluePlayerTurnIndicator(Graphics2D g2d) {
        try {
            g2d.fillRect((new panel().getPreferredSize().width / 2) - (width / 4), new panel().getPreferredSize().height - (width - (width / 4)), width - (width / 2), (int) (width - (width / 1.5)));
        }
        catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void redPlayerTurnIndicator(Graphics2D g2d) {
        try {
            g2d.fillRect((new panel().getPreferredSize().width / 2) - (width / 4), width - (width / 2), width - (width / 2), (int) (width - (width / 1.5)));
        }
        catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
}