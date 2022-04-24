import java.util.*;

class Player {

    static final int TYPE_MONSTER = 0;
    static final int TYPE_MY_HERO = 1;
    static final int TYPE_OP_HERO = 2;

    static class Entity {
        public Integer threatScore = 0;
        public Entity claim;
        public Entity target;
        int id;
        int type;
        int x, y;
        int shieldLife;
        int isControlled;
        int health;
        int vx, vy;
        int nearBase;
        int threatFor;

        Entity(int id, int type, int x, int y, int shieldLife, int isControlled, int health, int vx, int vy, int nearBase, int threatFor) {
            this.id = id;
            this.type = type;
            this.x = x;
            this.y = y;
            this.shieldLife = shieldLife;
            this.isControlled = isControlled;
            this.health = health;
            this.vx = vx;
            this.vy = vy;
            this.nearBase = nearBase;
            this.threatFor = threatFor;
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        // base_x,base_y: The corner of the map representing your base
        int baseX = in.nextInt();
        int baseY = in.nextInt();
        // heroesPerPlayer: Always 3
        int heroesPerPlayer = in.nextInt();

        // game loop
        while (true) {
            int myHealth = in.nextInt(); // Your base health
            int myMana = in.nextInt(); // Ignore in the first league; Spend ten mana to cast a spell
            int oppHealth = in.nextInt();
            int oppMana = in.nextInt();
            int entityCount = in.nextInt(); // Amount of heros and monsters you can see

            List<Entity> myHeroes = new ArrayList<>(entityCount);
            List<Entity> oppHeroes = new ArrayList<>(entityCount);
            List<Entity> monsters = new ArrayList<>(entityCount);
            for (int i = 0; i < entityCount; i++) {
                int id = in.nextInt();              // Unique identifier
                int type = in.nextInt();            // 0=monster, 1=your hero, 2=opponent hero
                int x = in.nextInt();               // Position of this entity
                int y = in.nextInt();
                int shieldLife = in.nextInt();      // Ignore for this league; Count down until shield spell fades
                int isControlled = in.nextInt();    // Ignore for this league; Equals 1 when this entity is under a control spell
                int health = in.nextInt();          // Remaining health of this monster
                int vx = in.nextInt();              // Trajectory of this monster
                int vy = in.nextInt();
                int nearBase = in.nextInt();        // 0=monster with no target yet, 1=monster targeting a base
                int threatFor = in.nextInt();       // Given this monster's trajectory, is it a threat to 1=your base, 2=your opponent's base, 0=neither

                Entity entity = new Entity(id, type, x, y, shieldLife, isControlled, health, vx, vy, nearBase, threatFor);
                switch (type) {
                    case TYPE_MONSTER:
                        monsters.add(entity);
                        break;
                    case TYPE_MY_HERO:
                        myHeroes.add(entity);
                        break;
                    case TYPE_OP_HERO:
                        oppHeroes.add(entity);
                        break;
                }
            }

            // rank monsters
            ArrayList<Entity> targetMonsters = new ArrayList<>();
            for (Entity monster : monsters) {
                Integer threatScore = 0;
                if (monster.threatFor == 1) {
                    threatScore = 500;
                    if (monster.nearBase == 1) {
                        monster.nearBase += 500;
                    }
                }

                double distance = Math.hypot(baseX - monster.x, baseY - monster.y);
                threatScore += Math.round((float) (500 * (1 / (distance + 1))));
                monster.threatScore = threatScore;

                Entity monsterWithLowestScore = targetMonsters.stream()
                        .min(Comparator.comparingInt(value -> value.threatScore))
                        .orElseGet(() -> null);
                if (monsterWithLowestScore == null) {
                    targetMonsters.add(monster);
                } else if (monster.threatScore > monsterWithLowestScore.threatScore) {
                    if (targetMonsters.size() >= 3) {
                        targetMonsters.remove(monsterWithLowestScore);
                    }
                    targetMonsters.add(monster);
                } else if(targetMonsters.size() < 3){
                    targetMonsters.add(monster);
                }
            }
            System.err.println("monsters found : " + monsters.size());

            // find each hero for each monster
            Map<Double, Map<Entity, Entity>> monsterToHeroesDistance = new TreeMap<>();
            for (Entity targetMonster : targetMonsters) {
                for (Entity myHero : myHeroes) {
                    double distance = Math.hypot(myHero.x - targetMonster.x, myHero.y - targetMonster.y);
                    monsterToHeroesDistance.computeIfAbsent(distance, k -> new HashMap()).put(targetMonster, myHero);
                }
            }
            System.err.println("target found : " + targetMonsters.size());
            for (Map<Entity, Entity> value : monsterToHeroesDistance.values()) {
                for (Entity monster : value.keySet()) {
                    Entity currentHeroTarget = value.get(monster).target;
                    if (currentHeroTarget == null && monster.target == null) {
                        value.get(monster).target = monster;
                        monster.target = value.get(monster);
                    }
                }
            }

            // Shout commands
            boolean uniqueWait = false;
            for (Entity myHero : myHeroes) {
                Entity target = myHero.target;
                if (target != null) {
                    System.out.println(String.format("MOVE %d %d", target.x, target.y));
                } else {
                    if (!uniqueWait) {
                        System.out.println("WAIT");
                        uniqueWait = true;
                    } else {
                        int movx = 0;
                        int movy = 0;
                        if (baseX == 0) {
                            movx = myHero.x + myHero.x;
                            movy = myHero.y + myHero.y;
                            double newHyp = Math.hypot(movx, movy);
                            if (newHyp > 15000) {
                                movx = myHero.x - myHero.x / 2;
                                movy = myHero.y - myHero.y / 2;
                            }
                        } else {
                            movx = myHero.x - (baseX - myHero.x);
                            movy = myHero.y - (baseY - myHero.y);
                            double newHyp = Math.hypot(baseX - movx, baseY - movy);
                            System.err.println("hyp : " + newHyp);
                            if (newHyp > 15000) {
                                movx = myHero.x + (baseX - myHero.x) / 2;
                                movy = myHero.y + (baseY - myHero.y) / 2;
                            }
                        }
                        System.out.println(String.format("MOVE %d %d", movx, movy));
                    }
                }
            }
        }
    }
}