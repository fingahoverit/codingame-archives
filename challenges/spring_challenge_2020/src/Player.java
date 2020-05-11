import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Grab the pellets as fast as you can!
 **/
class Player {

    static class TurnData {
        int myScore;
        int opponentScore;
        int visiblePacCount; // all your pacs and enemy pacs in sight
        int visiblePelletCount; // all pellets in sight
        Map<Integer, Pac> myPacs = new HashMap<>();
        Map<Integer, Pac> badPacs = new HashMap<>();

        public int getMyScore() {
            return myScore;
        }

        public void setMyScore(int myScore) {
            this.myScore = myScore;
        }

        public int getOpponentScore() {
            return opponentScore;
        }

        public void setOpponentScore(int opponentScore) {
            this.opponentScore = opponentScore;
        }

        public int getVisiblePacCount() {
            return visiblePacCount;
        }

        public void setVisiblePacCount(int visiblePacCount) {
            this.visiblePacCount = visiblePacCount;
        }

        public int getVisiblePelletCount() {
            return visiblePelletCount;
        }

        public void setVisiblePelletCount(int visiblePelletCount) {
            this.visiblePelletCount = visiblePelletCount;
        }

        public Map<Integer, Pac> getMyPacs() {
            return myPacs;
        }

        public void setMyPacs(Map<Integer, Pac> myPacs) {
            this.myPacs = myPacs;
        }

        public Map<Integer, Pac> getBadPacs() {
            return badPacs;
        }

        public void setBadPacs(Map<Integer, Pac> badPacs) {
            this.badPacs = badPacs;
        }
    }

    static class Pac {
        int pacId; // pac number (unique within a team)
        boolean mine; // true if this pac is yours
        int x; // position in the grid
        int y; // position in the grid
        String typeId; // unused in wood leagues
        int speedTurnsLeft; // unused in wood leagues
        int abilityCooldown; // unused in wood leagues
        PacBrain brain = new PacBrain();

        Pac(int pacId, boolean mine) {
            this.pacId = pacId;
            this.mine = mine;
        }

        public int getPacId() {
            return pacId;
        }

        public void setPacId(int pacId) {
            this.pacId = pacId;
        }

        public boolean isMine() {
            return mine;
        }

        public void setMine(boolean mine) {
            this.mine = mine;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public int getSpeedTurnsLeft() {
            return speedTurnsLeft;
        }

        public void setSpeedTurnsLeft(int speedTurnsLeft) {
            this.speedTurnsLeft = speedTurnsLeft;
        }

        public int getAbilityCooldown() {
            return abilityCooldown;
        }

        public void setAbilityCooldown(int abilityCooldown) {
            this.abilityCooldown = abilityCooldown;
        }

        public PacBrain getBrain() {
            return brain;
        }

        public void setBrain(PacBrain brain) {
            this.brain = brain;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Pac.class.getSimpleName() + "[", "]")
                    .add("pacId=" + pacId)
                    .add("mine=" + mine)
                    .add("x=" + x)
                    .add("y=" + y)
                    .add("typeId='" + typeId + "'")
                    .add("speedTurnsLeft=" + speedTurnsLeft)
                    .add("abilityCooldown=" + abilityCooldown)
                    .add("brain=" + brain)
                    .toString();
        }
    }

    static class PacBrain {
        static final String[] THOUGHTS = {"Itok.", "Zoktok.", "Ur-ur-ur!", "Glor'duk.", "Zug zug.", "Dabu.",
                "Lok'tar.", "Swobu."};
        static final int THOUGHT_FATIGUE = 5;

        int callCounter = 0;
        String currentThought = THOUGHTS[0];

        public String readMyThoughts() {
            if (callCounter > THOUGHT_FATIGUE) {
                currentThought = THOUGHTS[new Random().nextInt(THOUGHTS.length)];
                callCounter = 0;
            } else {
                callCounter++;
            }

            return currentThought;
        }
    }

    static class Target {
        int x;
        int y;

        Target(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    static class MapBuilder {
        static int[][] build(Scanner in) {

            int width = in.nextInt(); // size of the grid
            int height = in.nextInt(); // top left corner is (x=0, y=0)

            int[][] map = new int[width][height];

            if (in.hasNextLine()) {
                in.nextLine();
            }
            for (int rn = 0; rn < height; rn++) {
                String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
                char[] chars = row.toCharArray();
                for (int ln = 0; ln < chars.length; ln++) {
                    if ('#' == chars[ln]) {
                        map[ln][rn] = -1;
                    }
                }
            }

            return map;
        }
    }

    static class TurnDataUpdater {

        TurnData data = new TurnData();
        Scanner in;

        TurnDataUpdater(Scanner in) {
            this.in = in;
        }

        TurnData updateData(int[][] map) {

            // Update Pac Data
            data.setMyScore(in.nextInt());
            data.setOpponentScore(in.nextInt());

            // Update Pac Data
            data.setVisiblePacCount(in.nextInt()); // all your pacs and enemy pacs in sight

            for (int i = 0; i < data.getVisiblePacCount(); i++) {
                int pacId = in.nextInt();
                boolean mine = in.nextInt() != 0;

                Pac pac;
                if (mine) {
                    pac = Optional.ofNullable(data.getMyPacs().get(pacId)).orElseGet(() -> {
                        data.getMyPacs().put(pacId, new Pac(pacId, true));
                        return data.getMyPacs().get(pacId);
                    });
                } else {
                    pac = Optional.ofNullable(data.getBadPacs().get(pacId)).orElseGet(() -> {
                        data.getBadPacs().put(pacId, new Pac(pacId, false));
                        return data.getMyPacs().get(pacId);
                    });
                }

                pac.setX(in.nextInt());
                pac.setY(in.nextInt());
                pac.setTypeId(in.next());
                pac.setSpeedTurnsLeft(in.nextInt());
                pac.setAbilityCooldown(in.nextInt());
            }

            // Update Map
            data.setVisiblePelletCount(in.nextInt()); // all pellets in sight
            resetGameMap(map);
            for (int i = 0; i < data.getVisiblePelletCount(); i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                map[x][y] = in.nextInt(); // amount of points this pellet is worth
            }

            return data;
        }

        void resetGameMap(int[][] gameMap) {
            for (int x = 0; x < gameMap.length; x++) {
                for (int y = 0; y < gameMap[x].length; y++) {
                    if (gameMap[x][y] > -1) {
                        gameMap[x][y] = 0;
                    }
                }
            }
        }
    }

    static class TargetFinder {


        static Map<Integer, Target> findTarget(int[][] gameMap, TurnData data) {

            Map<Integer, Target> targets = new HashMap<>();
            data.getMyPacs().forEach((pacId, pac) -> {
                targets.put(pacId, findFirstPellet(gameMap));
            });
            return targets;
        }

        static Target findFirstPellet(int[][] gameMap) {

            boolean found = false;
            int x = 0;
            int y = 0;
            while (found == false && x < gameMap.length) {
                while (found == false && y < gameMap[x].length) {
                    if (gameMap[x][y] > 0) {
                        found = true;
                    } else {
                        y++;
                    }
                }
                if (!found) {
                    y = 0;
                    x++;
                }
            }

            return new Target(x, y);
        }
    }

    public static void main(String args[]) {

        Scanner in = new Scanner(System.in);

        // Build map
        int[][] gameMap = MapBuilder.build(in);

        TurnDataUpdater updater = new TurnDataUpdater(in);
        // game loop
        while (true) {

            // Update Turn Data
            TurnData data = updater.updateData(gameMap);

            // Find Target
            Map<Integer, Target> targets = TargetFinder.findTarget(gameMap, data);

            // Call !
            data.getMyPacs().forEach((key, pac) -> {
                String command = new StringJoiner(" ")
                        .add("MOVE")
                        .add(key.toString())
                        .add(String.valueOf(targets.get(key).getX()))
                        .add(String.valueOf(targets.get(key).getY()))
                        .add(key + "->" + targets.get(key).getX() + "/" + targets.get(key).getY() + ": \"" + pac.getBrain().readMyThoughts() + "\"")
                        .toString();
                System.out.println(command);
            });
        }
    }
}