import java.util.*;
import java.util.stream.Collectors;

/**
 * Grab the pellets as fast as you can!
 **/
class Player {

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
            Map<Integer, Position> targets = TargetFinder.findTargets(gameMap, data);

            // Call !

            String command = data.getMyPacs().values().stream().map(pac -> new StringJoiner(" ")
                    .add("MOVE")
                    .add(String.valueOf(pac.getPacId()))
                    .add(String.valueOf(targets.get(pac.getPacId()).getX()))
                    .add(String.valueOf(targets.get(pac.getPacId()).getY()))
                    .add(String.valueOf(pac.getPacId())
                            + (data.getTurnNumber() % 2 == 0
                            ? "->" + targets.get(pac.getPacId()).getX() + "/" + targets.get(pac.getPacId()).getY()
                            : ": \"" + pac.getBrain().readMyThoughts() + "\""))
                    .toString()).collect(Collectors.joining(" | "));
            System.out.println(command);
        }
    }

    static class TurnData {
        int turnNumber = 0;
        int myScore;
        int opponentScore;
        int visiblePacCount; // all your pacs and enemy pacs in sight
        int visiblePelletCount; // all pellets in sight
        Map<Integer, Pac> myPacs = new HashMap<>();
        Map<Integer, Pac> badPacs = new HashMap<>();

        public int getTurnNumber() {
            return turnNumber;
        }

        public void setTurnNumber(int turnNumber) {
            this.turnNumber = turnNumber;
        }

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
        int prevX; // previous position in the grid
        int prevY; // previous position in the grid
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

        public int getPrevX() {
            return prevX;
        }

        public void setPrevX(int prevX) {
            this.prevX = prevX;
        }

        public int getPrevY() {
            return prevY;
        }

        public void setPrevY(int prevY) {
            this.prevY = prevY;
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

    static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x &&
                    y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
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

            data.setTurnNumber(data.getTurnNumber() + 1);

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
                        return data.getBadPacs().get(pacId);
                    });
                }

                pac.setPrevX(pac.getX());
                pac.setPrevY(pac.getY());
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

        static Map<Integer, Position> findTargets(int[][] gameMap, TurnData data) {

            Set<Position> lockedTargets = new HashSet<>();
            Map<Integer, Position> targets = new HashMap<>();
            data.getMyPacs().forEach((pacId, pac) -> {
                // Collision
                if(pac.getX() == pac.getPrevX() && pac.getY() == pac.getPrevY()) {
                    lockedTargets.add(findFirstFreePellet(gameMap, lockedTargets));
                }
                Position target = findFirstFreePellet(gameMap, lockedTargets);
                lockedTargets.add(target);
                targets.put(pacId, target);
            });
            return targets;
        }

        static Position findFirstFreePellet(int[][] gameMap, Set<Position> lockedTargets) {

            int x = 0;
            int y = 0;
            while (x < gameMap.length) {
                while (y < gameMap[x].length) {
                    if (gameMap[x][y] > 0 && !lockedTargets.contains(new Position(x, y))) {
                        return new Position(x, y);
                    } else {
                        y++;
                    }
                }
                y = 0;
                x++;
            }

            return lockedTargets.iterator().next();
        }
    }
}