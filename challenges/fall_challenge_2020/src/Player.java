import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static final int MAX_DIFF_INGREDIENTS = 4;
    private List<Spell> spells = new ArrayList<>();
    private List<Recipe> recipes = new ArrayList<>();
    private Inventory inventory;
    private int score;

    public static void main(String[] args) {

        Player player = new Player();
        Player opponent = new Player();
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {

            // Load
            loadInput(in, player, opponent);

            // Find expensiver
            int id = findExpensiver(player);

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
            System.out.println("BREW " + id);
        }
    }

    private static int findExpensiver(Player player) {

        int id = 0;
        int expensiver = 0;
        for (Recipe recipe : player.getRecipes()) {
            if (expensiver < recipe.getPrice()) {

                expensiver = recipe.getPrice();
                id = recipe.getActionId();
            }
        }

        return id;
    }

    private static void loadInput(Scanner in, Player player, Player opponent) {
        int actionCount = in.nextInt(); // the number of spells and recipes in play
        for (int i = 0; i < actionCount; i++) {
            int actionId = in.nextInt(); // the unique ID of this spell or recipe
            String actionType = in.next(); // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW

            switch (ActionType.valueOf(actionType)) {
                case CAST:
                    addSpell(in, actionId, player);
                    break;
                case OPPONENT_CAST:
                    addSpell(in, actionId, opponent);
                    break;
                case BREW:
                    addRecipe(in, actionId, player);
                    break;
                default:
                    System.err.println("Unknown action type : " + actionType);
                    break;
            }
        }
        completePlayer(in, player);
        completePlayer(in, opponent);
    }

    private static void completePlayer(Scanner in, Player player) {

        Inventory inventory = new Inventory();

        for (int i = 0; i < MAX_DIFF_INGREDIENTS; i++) {
            inventory.addIngredient(in.nextInt());
        }
        player.setInventory(inventory);
        player.setScore(in.nextInt()); // amount of rupees
    }

    private static void addSpell(Scanner in, int actionId, Player player) {

        Spell spell = new Spell();

        spell.setActionId(actionId);
        for (int i = 0; i < MAX_DIFF_INGREDIENTS; i++) {
            spell.addIngredient(in.nextInt());
        }
        spell.setPrice(in.nextInt()); // the price in rupees if this is a potion
        spell.setTomeIndex(in.nextInt()); // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
        spell.setTaxCount(in.nextInt()); // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
        spell.setCastable(in.nextInt() != 0); // in the first league: always 0; later: 1 if this is a castable player spell
        spell.setRepeatable(in.nextInt() != 0); // for the first two leagues: always 0; later: 1 if this is a repeatable player spell

        player.getSpells().add(spell);
    }

    private static void addRecipe(Scanner in, int actionId, Player player) {

        Recipe recipe = new Recipe();
        recipe.setActionId(actionId);
        for (int i = 0; i < MAX_DIFF_INGREDIENTS; i++) {
            recipe.addIngredient(in.nextInt());
        }
        recipe.setPrice(in.nextInt()); // the price in rupees if this is a potion
        recipe.setTomeIndex(in.nextInt()); // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
        recipe.setTaxCount(in.nextInt()); // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
        recipe.setCastable(in.nextInt() != 0); // in the first league: always 0; later: 1 if this is a castable player spell
        recipe.setRepeatable(in.nextInt() != 0); // for the first two leagues: always 0; later: 1 if this is a repeatable player spell

        player.getRecipes().add(recipe);
    }

    public List<Spell> getSpells() {
        return spells;
    }

    public void setSpells(List<Spell> spells) {
        this.spells = spells;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    enum ActionType {
        CAST, OPPONENT_CAST, BREW;
    }

    public static class Spell {
        int actionId;
        List<Integer> ingredients = new ArrayList<>();
        int price;
        int tomeIndex;
        int taxCount;
        boolean castable;
        boolean repeatable;

        public int getActionId() {
            return actionId;
        }

        public void setActionId(int actionId) {
            this.actionId = actionId;
        }

        public List<Integer> getIngredients() {
            return ingredients;
        }

        public void setIngredients(List<Integer> ingredients) {
            this.ingredients = ingredients;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getTomeIndex() {
            return tomeIndex;
        }

        public void setTomeIndex(int tomeIndex) {
            this.tomeIndex = tomeIndex;
        }

        public int getTaxCount() {
            return taxCount;
        }

        public void setTaxCount(int taxCount) {
            this.taxCount = taxCount;
        }

        public boolean isCastable() {
            return castable;
        }

        public void setCastable(boolean castable) {
            this.castable = castable;
        }

        public boolean isRepeatable() {
            return repeatable;
        }

        public void setRepeatable(boolean repeatable) {
            this.repeatable = repeatable;
        }

        public void addIngredient(Integer ingredient) {
            ingredients.add(ingredient);
        }

        public boolean isCastable(Inventory inventory) {
            return castable && inventory.isEnough(this.ingredients);
        }
    }

    public static class Recipe {
        int actionId;
        List<Integer> ingredients = new ArrayList<>();
        int price;
        int tomeIndex;
        int taxCount;
        boolean castable;
        boolean repeatable;

        public int getActionId() {
            return actionId;
        }

        public void setActionId(int actionId) {
            this.actionId = actionId;
        }

        public List<Integer> getIngredients() {
            return ingredients;
        }

        public void setIngredients(List<Integer> ingredients) {
            this.ingredients = ingredients;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getTomeIndex() {
            return tomeIndex;
        }

        public void setTomeIndex(int tomeIndex) {
            this.tomeIndex = tomeIndex;
        }

        public int getTaxCount() {
            return taxCount;
        }

        public void setTaxCount(int taxCount) {
            this.taxCount = taxCount;
        }

        public boolean isCastable() {
            return castable;
        }

        public void setCastable(boolean castable) {
            this.castable = castable;
        }

        public boolean isRepeatable() {
            return repeatable;
        }

        public void setRepeatable(boolean repeatable) {
            this.repeatable = repeatable;
        }

        public void addIngredient(Integer ingredient) {
            ingredients.add(ingredient);
        }

        public boolean isCookable(Inventory inventory) {
            return inventory.isEnough(this.ingredients);
        }
    }

    public static class Inventory {
        List<Integer> ingredients = new ArrayList<>();

        public void addIngredient(Integer ingredient) {
            ingredients.add(ingredient);
        }

        public boolean isEnough(List<Integer> deltas) {
            return false;
        }
    }


}