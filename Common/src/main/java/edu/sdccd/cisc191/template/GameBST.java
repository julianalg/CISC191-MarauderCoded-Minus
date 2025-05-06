package edu.sdccd.cisc191.template;

import edu.sdccd.cisc191.template.Game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class BSTNode<T> {
    T data;
    BSTNode<T> left, right;

    BSTNode(T data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}

public class GameBST {

    public static class BinarySearchTree<T> {
        private BSTNode<T> root;
        private Comparator<T> comparator;

        public BinarySearchTree(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        public void insert(T data) {
            root = insertRecursive(root, data);
        }

        private BSTNode<T> insertRecursive(BSTNode<T> node, T data) {
            if (node == null) return new BSTNode<>(data);
            if (comparator.compare(data, node.data) < 0) {
                node.left = insertRecursive(node.left, data);
            } else {
                node.right = insertRecursive(node.right, data);
            }
            return node;
        }

        public List<T> inorderTraversal() {
            List<T> result = new ArrayList<>();
            inorderRecursive(root, result);
            return result;
        }

        private void inorderRecursive(BSTNode<T> node, List<T> list) {
            if (node != null) {
                inorderRecursive(node.left, list);
                list.add(node.data);
                inorderRecursive(node.right, list);
            }
        }
    }

    public static BinarySearchTree<Game> buildGameIdTree(List<Game> games) {
        BinarySearchTree<Game> treeById = new BinarySearchTree<>(Comparator.comparingInt(Game::getId));
        for (Game game : games) {
            treeById.insert(game);
        }
        return treeById;
    }

    public static BinarySearchTree<Game> buildOddsTree(List<Game> games) {
        BinarySearchTree<Game> treeByOdds = new BinarySearchTree<>(Comparator.comparingDouble(Game::getTeam1Odd));
        for (Game game : games) {
            treeByOdds.insert(game);
        }
        return treeByOdds;
    }

    public static BinarySearchTree<Game> buildTeam2OddsTree(List<Game> games) {
        BinarySearchTree<Game> treeByTeam2Odds = new BinarySearchTree<>(Comparator.comparingDouble(Game::getTeam2Odd));
        for (Game game : games) {
            treeByTeam2Odds.insert(game);
        }
        return treeByTeam2Odds;
    }
}
