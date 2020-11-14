import java.util.*;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static final int MAX_INVENTORY_SIZE = 10;
    public static final int MAX_DIFF_INGREDIENTS = 4;
    public static final int NOT_FOUND = -1;
    private List<Spell> spells = new ArrayList<>();
    private List<Recipe> recipes = new ArrayList<>();
    private Inventory inventory;
    private int score;

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {

            int id = -1;
            Command command = Command.WAIT;
            Player player = new Player();
            Player opponent = new Player();

            // Load
            loadInput(in, player, opponent);

            // display input
            System.err.println(" Player > " + player.toString());
            System.err.println(" Opponent > " + opponent.toString());

            // Find Recipe to cook
            id = findRecipe(player, opponent);

            // Find Spell to cast
            if (id != NOT_FOUND) {
                command = Command.BREW;
            } else {
                id = findSpell(player, opponent);

                if (id != NOT_FOUND) {
                    command = Command.CAST;
                } else {
                    command = Command.REST;
                }
            }

            // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
            System.out.println(generateCommand(id, command));
        }
    }

    private static String generateCommand(int id, Command command) {
        if (command == Command.REST || command == Command.WAIT) {
            return command.name();
        }
        return command.name() + " " + id;
    }

    private static int findSpell(Player player, Player opponent) {
        Collections.reverse(player.getSpells());
        for (Spell spell : player.getSpells()) {
            if (spell.isCastable(player.getInventory())) {

                // Filter cast
                if (player.getInventory().returnMaxNbOfTransformedIngredientAfterCast(spell.getIngredients()) < 3) {
                    return spell.getActionId();
                }
            }
        }
        return NOT_FOUND;
    }

    private static int findRecipe(Player player, Player opponent) {
        for (Recipe recipe : player.getRecipes().stream().sorted(Comparator.comparingInt(Recipe::getPrice)).collect(Collectors.toList())) {
            if (recipe.isCookable(player.getInventory())) {
                return recipe.getActionId();
            }
        }
        return NOT_FOUND;
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

    @Override
    public String toString() {
        return new StringJoiner(", ", Player.class.getSimpleName() + "[", "]")
                .add("spells=" + spells)
                .add("recipes=" + recipes)
                .add("inventory=" + inventory)
                .add("score=" + score)
                .toString();
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
        CAST, OPPONENT_CAST, BREW
    }

    enum Command {
        BREW, CAST, REST, WAIT
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

        @Override
        public String toString() {
            return new StringJoiner(", ", Spell.class.getSimpleName() + "[", "]")
                    .add("actionId=" + actionId)
                    .add("ingredients=" + ingredients)
                    .add("price=" + price)
                    .add("tomeIndex=" + tomeIndex)
                    .add("taxCount=" + taxCount)
                    .add("castable=" + castable)
                    .add("repeatable=" + repeatable)
                    .toString();
        }

        public void addIngredient(Integer ingredient) {
            ingredients.add(ingredient);
        }

        public int sumIngredients() {
            return ingredients.stream().mapToInt(Integer::intValue).sum();
        }

        public boolean isCastable(Inventory inventory) {
            return castable && inventory.isEnough(this.ingredients) && (sumIngredients() + inventory.sumIngredients() < MAX_INVENTORY_SIZE);
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

        @Override
        public String toString() {
            return new StringJoiner(", ", Recipe.class.getSimpleName() + "[", "]")
                    .add("actionId=" + actionId)
                    .add("ingredients=" + ingredients)
                    .add("price=" + price)
                    .add("tomeIndex=" + tomeIndex)
                    .add("taxCount=" + taxCount)
                    .add("castable=" + castable)
                    .add("repeatable=" + repeatable)
                    .toString();
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

        // Stats
        int currentMaxIngredient = 0;

        @Override
        public String toString() {
            return new StringJoiner(", ", Inventory.class.getSimpleName() + "[", "]")
                    .add("ingredients=" + ingredients)
                    .toString();
        }

        public void addIngredient(Integer ingredient) {
            ingredients.add(ingredient);
        }

        public int sumIngredients() {
            return ingredients.stream().mapToInt(Integer::intValue).sum();
        }

        public int getCurrentMaxIngredient() {
            if (currentMaxIngredient == 0) {
                currentMaxIngredient = ingredients.stream().mapToInt(Integer::intValue).max().orElse(0);
            }

            return currentMaxIngredient;
        }

        public int returnMaxNbOfTransformedIngredientAfterCast(List<Integer> deltas) {

            int maxNbIngredient = 0;
            for (int i = 1; i < deltas.size(); i++) {
                int nbIngredient = this.ingredients.get(i) + deltas.get(i);
                if (nbIngredient > maxNbIngredient) {
                    maxNbIngredient = nbIngredient;
                }
            }
            return maxNbIngredient;
        }

        public boolean isEnough(List<Integer> deltas) {
            for (int i = 0; i < deltas.size(); i++) {
                if (this.ingredients.get(i) + deltas.get(i) < 0) {
                    return false;
                }
            }
            return true;
        }
    }
}