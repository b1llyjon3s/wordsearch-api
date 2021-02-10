package com.bilal.wordgrid.services;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class WordGridService {
    /**
     * Direction enum
     */
    private enum Direction{
        HORIZONTAL,
        VERTICAL,
        DIAGONAL,
        HORIZONTAL_INVERSE,
        VERTICAL_INVERSE,
        DIAGONAL_INVERSE
    }
    /**
     * Coordinate class
     */
    private class Coordinate{
        int x, y;
        boolean visited;
        Coordinate(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    /**
     * @param words
     * Fill the grid with the words while ensuring proper formatting
     * with the use of the Coordinate class and the Direction enum.
     */
    public char[][] generateGrid(int gridSize, List<String> words){
        //a list that holds all the coordinates - to be used in the fillGrid method.
        List<Coordinate> coordinates = new ArrayList<>();
        char[][] grid = new char[gridSize][gridSize];
        //initialize the grid
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                coordinates.add(new Coordinate(i,j));
                grid[i][j] = '_';
            }
        }
        //generate the grid by iterating over the list of words
        for(String word: words) {
            if (word.length() >= gridSize || word.length() <= 2) continue;
            //random starting point
            Collections.shuffle(coordinates);
            //iterate through coordinates
            for (Coordinate coordinate : coordinates) {
                int x = coordinate.x;
                int y = coordinate.y;
                //iterate through directions and find a suitable direction for a given coord
                Direction direction = fitDirection(grid, word, coordinate);
                if(direction != null){
                    for (char c : word.toUpperCase().toCharArray()) {
                        switch (direction) {
                            case HORIZONTAL:
                                grid[x][y++] = c;
                                break;
                            case VERTICAL:
                                grid[x++][y] = c;
                                break;
                            case DIAGONAL:
                                grid[x++][y++] = c;
                                break;
                            case HORIZONTAL_INVERSE:
                                grid[x][y--] = c;
                                break;
                            case VERTICAL_INVERSE:
                                grid[x--][y] = c;
                                break;
                            case DIAGONAL_INVERSE:
                                grid[x--][y--] = c;
                                break;
                        }
                    }
                    //direction and coordinate found - thus move on to the next word
                    break;
                }
            }
        }
        //fill the empty ('_') slots with random letters
        randomFillGrid(grid);
        return grid;
    }

    /**
     * Displays the grid
     */
    public void displayGrid(char[][] grid){
        int gridSize = grid[0].length;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Fill the filler coordinates with random letters
     */
    private void randomFillGrid(char[][] grid){
        int gridSize = grid[0].length;
        String allCapsLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i <gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if(grid[i][j] == '_'){
                    int randomIndex = ThreadLocalRandom.current().nextInt(0,allCapsLetters.length());
                    grid[i][j] = allCapsLetters.charAt(randomIndex);
                }
            }
        }
    }

    /**
     *
     * @param word
     * @param coordinate
     * @return The direction in which the word can fit or null if the word cannot fit in any direction
     */
    private Direction fitDirection(char[][] grid, String word, Coordinate coordinate){
        List<Direction> directions = Arrays.asList(Direction.values());
        Collections.shuffle(directions);
        //check if fits in a direction
        for(Direction direction : directions){
            if(doesFit(grid, word, coordinate, direction))return direction;
        }
        return null;
    }

    /**
     *
     * @param word
     * @param coordinate
     * @param direction
     * @return Whether the word fits in the given direction.
     */
    private boolean doesFit(char[][] grid,String word, Coordinate coordinate, Direction direction){
        int gridSize = grid[0].length;
        switch(direction){
            case HORIZONTAL:
                if(coordinate.y + word.length() >= gridSize) return false;
                for (int i = 0; i < word.length(); i++) {
                    char letter = grid[coordinate.x][coordinate.y + i];
                    if(letter != '_' && letter != word.charAt(i) ) return false;
                }
                break;
            case VERTICAL:
                if(coordinate.x + word.length() >= gridSize) return false;
                for (int i = 0; i < word.length(); i++) {
                    char letter = grid[coordinate.x+i][coordinate.y];
                    if(letter != '_' && letter != word.charAt(i) ) return false;
                }
                break;
            case DIAGONAL:
                if(coordinate.y + word.length() >= gridSize || coordinate.x + word.length() >= gridSize) return false;
                for (int i = 0; i < word.length(); i++) {
                    char letter = grid[coordinate.x + i][coordinate.y + i];
                    if(letter != '_' && letter != word.charAt(i) ) return false;
                }
                break;
            case HORIZONTAL_INVERSE:
                if(coordinate.y < word.length()) return false;
                for (int i = 0; i < word.length(); i++) {
                    char letter = grid[coordinate.x][coordinate.y - i];
                    if(letter != '_' && letter != word.charAt(i) ) return false;
                }
                break;
            case VERTICAL_INVERSE:
                if(coordinate.x < word.length() ) return false;
                for (int i = 0; i < word.length(); i++) {
                    char letter = grid[coordinate.x-i][coordinate.y];
                    if(letter != '_' && letter != word.charAt(i) ) return false;
                }
                break;
            case DIAGONAL_INVERSE:
                if(coordinate.y < word.length() || coordinate.x < word.length()) return false;
                for (int i = 0; i < word.length(); i++) {
                    char letter = grid[coordinate.x - i][coordinate.y - i];
                    if(letter != '_' && letter != word.charAt(i) ) return false;
                }
                break;

        }
        return true;
    }

}
