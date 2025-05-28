package edu.sdccd.cisc191.Common;

import edu.sdccd.cisc191.Common.GameBST.BinarySearchTree;
import edu.sdccd.cisc191.Common.Models.Game;
import org.joda.time.DateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameBSTTest {

    @Test
    @DisplayName("Generic BST inorderTraversal should sort Integers")
    void testGenericBinarySearchTreeInorderTraversal() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>(Integer::compareTo);
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);
        tree.insert(1);
        tree.insert(4);

        List<Integer> expected = Arrays.asList(1, 3, 4, 5, 7);
        assertEquals(expected, tree.inorderTraversal(),
                "Inorder traversal of [5,3,7,1,4] should be [1,3,4,5,7]");
    }

    @Test
    @DisplayName("buildGameIdTree orders by Game.getId()")
    void testBuildGameIdTree() {
        Game g1 = new Game();
        g1.setId(2L);
        Game g2 = new Game();
        g2.setId(1L);

        List<Game> games = Arrays.asList(g1, g2);
        BinarySearchTree<Game> tree = GameBST.buildGameIdTree(games);

        List<Game> inorder = tree.inorderTraversal();
        assertEquals(1L, inorder.get(0).getId(), "First element should have ID 1");
        assertEquals(2L, inorder.get(1).getId(), "Second element should have ID 2");
    }

    @Test
    @DisplayName("buildOddsTree orders by Game.getTeam1Odd()")
    void testBuildOddsTree() {
        Game g1 = new Game();
        g1.setTeam1Odd(2.0);
        Game g2 = new Game();
        g2.setTeam1Odd(1.5);

        List<Game> games = Arrays.asList(g1, g2);
        BinarySearchTree<Game> tree = GameBST.buildOddsTree(games);

        List<Game> inorder = tree.inorderTraversal();
        assertEquals(1.5, inorder.get(0).getTeam1Odd(), 1e-9,
                "Lowest team1Odd should come first");
        assertEquals(2.0, inorder.get(1).getTeam1Odd(), 1e-9,
                "Highest team1Odd should come last");
    }

    @Test
    @DisplayName("buildTeam2OddsTree orders by Game.getTeam2Odd()")
    void testBuildTeam2OddsTree() {
        Game g1 = new Game();
        g1.setTeam2Odd(3.0);
        Game g2 = new Game();
        g2.setTeam2Odd(2.5);

        List<Game> games = Arrays.asList(g1, g2);
        BinarySearchTree<Game> tree = GameBST.buildTeam2OddsTree(games);

        List<Game> inorder = tree.inorderTraversal();
        assertEquals(2.5, inorder.get(0).getTeam2Odd(), 1e-9,
                "Lowest team2Odd should come first");
        assertEquals(3.0, inorder.get(1).getTeam2Odd(), 1e-9,
                "Highest team2Odd should come last");
    }

    @Test
    @DisplayName("buildDateTree orders by Game.getGameDate()")
    void testBuildDateTree() {
        Game g1 = new Game();
        g1.setGameDate(new DateTime(2021, 1, 2, 0, 0));
        Game g2 = new Game();
        g2.setGameDate(new DateTime(2021, 1, 1, 0, 0));

        List<Game> games = Arrays.asList(g1, g2);
        BinarySearchTree<Game> tree = GameBST.buildDateTree(games);

        List<Game> inorder = tree.inorderTraversal();
        assertEquals(new DateTime(2021, 1, 1, 0, 0), inorder.get(0).getGameDate(),
                "Earliest date should come first");
        assertEquals(new DateTime(2021, 1, 2, 0, 0), inorder.get(1).getGameDate(),
                "Latest date should come last");
    }
}
