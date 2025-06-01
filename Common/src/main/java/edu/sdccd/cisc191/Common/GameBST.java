package edu.sdccd.cisc191.Common;

import edu.sdccd.cisc191.Common.Models.Game;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A utility class for constructing binary search trees (BSTs) of {@link Game} objects
 * based on various sorting criteria (ID, odds, date).
 */
public class GameBST {

    /**
     * A node in a binary search tree, holding a piece of data and references to left and right children.
     *
     * @param <T> the type of data stored in the node
     */
    static class BSTNode<T> {
        T data;
        BSTNode<T> left, right;

        /**
         * Creates a new node containing the specified data.
         *
         * @param data the data to store in this node
         */
        BSTNode(T data) {
            this.data = data;
            this.left = null;
            this.right = null;
            // TODO: Add null check for data to avoid inserting null nodes
        }
    }

    /**
     * A generic binary search tree implementation.
     *
     * @param <T> the type of elements maintained by this tree
     */
    public static class BinarySearchTree<T> {
        private BSTNode<T> root;
        private final Comparator<T> comparator;

        /**
         * Constructs an empty binary search tree that uses the given comparator
         * for ordering elements.
         *
         * @param comparator the comparator to determine the order of elements
         */
        public BinarySearchTree(Comparator<T> comparator) {
            this.comparator = comparator;
            // TODO: Check if comparator is null before using it
        }

        /**
         * Inserts a new element into the tree.
         *
         * @param data the element to insert
         */
        public void insert(T data) {
            root = insertRecursive(root, data);
            // TODO: Consider handling duplicates if needed (currently goes to right subtree)
        }

        /**
         * Recursively inserts a new element into the subtree rooted at the given node.
         *
         * @param node the root of the subtree
         * @param data the element to insert
         * @return the root of the modified subtree
         */
        private BSTNode<T> insertRecursive(BSTNode<T> node, T data) {
            if (node == null) {
                return new BSTNode<>(data);
            }
            if (comparator.compare(data, node.data) < 0) {
                node.left = insertRecursive(node.left, data);
            } else {
                node.right = insertRecursive(node.right, data);
            }
            return node;
        }

        /**
         * Returns a list of all elements in ascending order, as determined by the comparator.
         *
         * @return a list of the tree's elements in sorted (in-order) order
         */
        public List<T> inorderTraversal() {
            List<T> result = new ArrayList<>();
            inorderRecursive(root, result);
            return result;
            // TODO: Consider adding other traversal methods like preorder or postorder
        }

        /**
         * Recursively performs an in-order traversal of the subtree rooted at the given node,
         * adding elements to the provided list.
         *
         * @param node the root of the subtree
         * @param list the list to collect elements in sorted order
         */
        private void inorderRecursive(BSTNode<T> node, List<T> list) {
            if (node != null) {
                inorderRecursive(node.left, list);
                list.add(node.data);
                inorderRecursive(node.right, list);
            }
        }
    }

    /**
     * Builds a binary search tree of {@link Game} objects sorted by game ID.
     *
     * @param games the list of games to insert into the tree
     * @return a BST with games ordered by ID
     */
    public static BinarySearchTree<Game> buildGameIdTree(List<Game> games) {
        BinarySearchTree<Game> treeById = new BinarySearchTree<>(Comparator.comparing(Game::getId));
        games.forEach(treeById::insert);
        // TODO: Add null check for games list to avoid NullPointerException
        return treeById;
    }

    /**
     * Builds a binary search tree of {@link Game} objects sorted by the first team's betting odds.
     *
     * @param games the list of games to insert into the tree
     * @return a BST with games ordered by team1 odds
     */
    public static BinarySearchTree<Game> buildOddsTree(List<Game> games) {
        BinarySearchTree<Game> treeByOdds = new BinarySearchTree<>(Comparator.comparing(Game::getTeam1Odd));
        games.forEach(treeByOdds::insert);
        return treeByOdds;
    }

    /**
     * Builds a binary search tree of {@link Game} objects sorted by the second team's betting odds.
     *
     * @param games the list of games to insert into the tree
     * @return a BST with games ordered by team2 odds
     */
    public static BinarySearchTree<Game> buildTeam2OddsTree(List<Game> games) {
        BinarySearchTree<Game> treeByTeam2Odds = new BinarySearchTree<>(Comparator.comparing(Game::getTeam2Odd));
        games.forEach(treeByTeam2Odds::insert);
        return treeByTeam2Odds;
    }

    /**
     * Builds a binary search tree of {@link Game} objects sorted by the game date.
     *
     * @param games the list of games to insert into the tree
     * @return a BST with games ordered by date
     */
    public static BinarySearchTree<Game> buildDateTree(List<Game> games) {
        BinarySearchTree<Game> treeByDate = new BinarySearchTree<>(Comparator.comparing(Game::getGameDate));
        games.forEach(treeByDate::insert);
        return treeByDate;
    }
}
