package maze;


import java.util.*;

/**
 *  Contains the maze struture, which is just an array of 
 *  <code>MazeCell</code>s.  Also contains the algorithms
 *  for generating and solving the maze.
 *
 */
public class Maze {

    private int rows, cols;
    private MazeCell maze[][];
    //the UI code is all in MazeViewer.java
    private MazeViewer viewer;
    //This is just for random number generation
    private Random generator;
    private MazeCell startCell;
    private MazeCell endCell;
    private DisjointSet disjointSet;

    /**
     *  Creates a maze that has the given number of rows and columns.
     *  Sets the neighbors of each cell.
     *  @param rows  Number of rows in the maze.
     *  @param cols  Number of columns in the maze.
     */
    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        generator = new Random();

        // Create the maze.     
        maze = new MazeCell[rows][cols];
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                maze[i][j] = new MazeCell();
            }
        }

        // Set the neighbors for each cell in the maze.
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                MazeCell n, e, s, w;
                if (i == 0) n = null; // On north border of maze
                else n = maze[i-1][j]; 

                if (i == rows-1) s = null; // On south border of maze
                else s = maze[i+1][j];

                if (j == 0) w = null; // On west border of maze
                else w = maze[i][j-1];

                if (j == cols-1) e = null; // On east border of maze
                else e = maze[i][j+1];

                maze[i][j].setNeighbors(n,e,s,w);
            }
        }
    }


    /**
     *  Accessor that sets the <code>MazeViewer</code> variable for
     *  the maze.  When the viewer is not set, no visualization takes place.
     *  @param viewer Visual display place for the maze.
     */
    public void setViewer(MazeViewer viewer) {
        this.viewer = viewer;
    }

    /**
     *  Accessor that sets the start cell for the maze.
     *  @param cell Start cell for the maze.
     */
    public void setStartCell(MazeCell cell) {
       startCell = cell;
    }

    /**
     *  Accessor that sets the end cell for the maze.
     *  @param cell End cell for the maze.
     */
    public void setEndCell(MazeCell cell) {
        endCell = cell;
    }

    /**
     *  Accessor that returns the start cell for the maze.
     *  @return Start cell for the maze.
     */
    public MazeCell getStartCell() {
        return startCell;
    }

    /**
     *  Accessor that returns the end cell for the maze.
     *  @return End cell for the maze.
     */
    public MazeCell getEndCell() {
        return endCell;
    }

    /**
     *  Accessor that returns the number of rows in the maze.
     *  @return The number of rows in the maze.
     */
    public int getRows() {
        return rows;
    }

    /**
     *  Accessor that returns the number of columns in the maze.
     *  @return The number of columns in the maze.
     */
    public int getCols() {
        return cols;
    }

    /**
     *  Returns the cell in the maze at the given coordinates.
     *  @param row  The row in the maze of the cell.
     *  @param col  The column in the maze of the cell.
     *  @return  The cell at (<code>row</code>, <code>col</code>)
     */
    public MazeCell getCell(int row, int col) {
        //TODO - correct this.
        return maze[row][col];
    }

    /**
     *  Tells the viewer to show the maze again, with 
     *  any changes to cells updated.  The current cell 
     *  will be colored in the viewer.  If the viewer is null,
     *  this method does nothing.
     *  @param cell Current cell, that the viewer will color.
     */
    public synchronized void visualize(MazeCell cell) {
        //TODO - call the appropriate method from MazeViewer to visualize
    	viewer.visualize(cell);
    }

    /**
     *  Generates the maze. 
     *  The maze is generated by Kruskal's algorithm
     */
    public synchronized void generateMaze() {
        makeKruskalMaze();
        //you can change the startCell and endCell values
        startCell = maze[0][0];
        endCell = maze[rows-1][cols-1];
    }
    

    /**
     *  Forms the maze via Kruskal's algorithm.
     */
    public synchronized void makeKruskalMaze() {
        //TODO - use a modified version of Kruskal's algorithm to make the maze
    	disjointSet = new DisjointSet();
    	disjointSet.makeSet(maze);
    	int num = 0;
    	while(num < maze.length * maze[0].length - 1) {
    		int randX = generator.nextInt(maze.length);
    		int randY = generator.nextInt(maze[0].length);
    		MazeCell current = maze[randX][randY];
    		MazeCell neighbor = current.getRandomNeighbor();
            if(neighbor!= null && current.hasWall(neighbor)){
            	MazeCell currParent = disjointSet.find(current);
            	MazeCell neighborParent = disjointSet.find(neighbor);
            	if(!currParent.equals(neighborParent)){
            		disjointSet.union(currParent, neighborParent);
            		current.knockDownWall(neighbor);
            		num++;
            	}
            }
    		
    	}
    }
    

    /**
     *  Solve maze.  The input parameter is guaranteed
     *  to be one of "dfs", "bfs",  or "random".
     *  @param method The method for solving the maze; one of
     *                "dfs" = depth first search, 
     *                "bfs" = breadth first search, 
     *                "random" = random walk.
     */
    public synchronized void solveMaze(String method) {
    	
        //TODO - call the appropriate solution method
    	if(method.equals("random")){
    		solveRandomMaze();
    	}
    	else if(method.equals("dfs")){
    		solveDFSMaze();
    	}
    	else if(method.equals("bfs")){
    		solveBFSMaze();
    	}
    }

    /**
     *  Solves the maze by randomly choosing a neighboring
     *  cell to explore. This method has been written for you.
     *  Please note this method takes a very long time
     *  to complete.
     */
    public synchronized void solveRandomMaze() {
        // Start the search at the start cell
    	long startTime = System.currentTimeMillis();
        MazeCell current = startCell;

        // while we haven't reached the end of the maze
        while(current != endCell) { 
            visualize(current); // show the progress visually (repaint)
            MazeCell neighbors[] = current.getNeighbors();
            int index = generator.nextInt(neighbors.length);
            current.examine();
            current = neighbors[index];  
        }
        visualize(current);
    	long estimatedTime = System.currentTimeMillis() - startTime;
    	System.out.println("Random time used: " + estimatedTime);
    }

    /**
     *  Solves the maze by depth first search.
     */
    public synchronized void solveDFSMaze() {
    	long startTime = System.currentTimeMillis();
        //TODO - do a DFS implementation
    	for(int i = 0; i < maze.length; i++){
    		for(int j = 0 ; j < maze[0].length; j++){
    			MazeCell u = maze[i][j];
    			if(!u.visited()){
    				solveDFSMazeVisit(u);
    			}
    		}
    	}
    	visualize(endCell);
    	long estimatedTime = System.currentTimeMillis() - startTime;
    	System.out.println("DFS time used: " + estimatedTime);

    }
    
    public synchronized void solveDFSMazeVisit(MazeCell u){
    	u.visit();
    	for(MazeCell neighbor : u.getNeighbors()){
    		if(!neighbor.visited()){
    			solveDFSMazeVisit(neighbor);    		
    		}
    	}
    	u.examine();
    }
    

    /**
     *  Solves the maze by breadth first search.
     *  starts at the start vertex and stops when bfs
     *  discovers the end vertex
     */
    public synchronized void solveBFSMaze() {
    	long startTime = System.currentTimeMillis();
        //TODO - do a BFS implementation
    	startCell.visit();
    	Queue<MazeCell> Q = new LinkedList<MazeCell>();
    	Q.offer(startCell);
    	while(!Q.isEmpty()) { 
    		MazeCell u = Q.remove();
    		for(MazeCell neighbor : u.getNeighbors()){
    			if(!neighbor.visited()){
    				neighbor.visit();
    				Q.offer(neighbor);
    			}
    		}
    		u.examined();
    	}
    	visualize(endCell);
    	long estimatedTime = System.currentTimeMillis() - startTime;
    	System.out.println("BFS time used: " + estimatedTime);
    }
    

}
