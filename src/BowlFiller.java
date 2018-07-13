import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;

import java.awt.*;

@ScriptMeta(developer = "ADivorcedFork", desc = "Fills bowls with water", name = "Fork's Bowl Filler", version = 0.01)
public class BowlFiller extends Script implements RenderListener {
    //GUI
    private final Color color1 = new Color(255, 255, 255);
    private final Color color2 = new Color(0, 0, 0);
    private final Font font1 = new Font("Segoe UI Historic", 0, 11);
    private final Font font2 = new Font("Segoe UI Historic", 1, 11);
    private final Font font3 = new Font("Segoe UI Emoji", 0, 12);

    private String status = "Starting";
    private int bowlsFilled = 0;
    final long startTime = System.nanoTime();

    @Override
    public void onStart() {
        Log.info("Welcome to Fork's Bowl Filler");
        Log.info("Make sure you have empty bowls in your inventory for the script to run properly");
        Log.info("Also log in to World 371 or alternative PvP world");

        if (!Movement.isRunEnabled()) {
            Movement.toggleRun(true);
        }
    }

    @Override
    public int loop() {
        if (Inventory.contains("Bowl")) {
            status = "Filling bowls";
            SceneObject waterFountain = SceneObjects.getNearest(879);
            if (Players.getLocal().getAnimation() != -1) {
                Time.sleepUntil(() -> !Inventory.contains("Bowl"), 20000);
            } else if (waterFountain != null && Inventory.isItemSelected()) {
               waterFountain.interact("Use");
               Time.sleep(3000);
           } else {
               Inventory.getFirst("Bowl").interact("Use");
           }
        } else {
            status = "Banking";
            SceneObject bankChest = SceneObjects.getNearest(7411);
            if (bankChest != null) {
                bankChest.interact(s -> true);
                Time.sleep(1000);
                if (Bank.isOpen()) {
                    Bank.depositInventory();
                    bowlsFilled += 28;
                    Time.sleep(1000);
                    if (Bank.contains("Bowl")) {
                        Bank.withdrawAll("Bowl");
                        Time.sleep(1000);
                        Bank.close();
                    } else {
                        Log.info("No more bowls available. Script stopping.");
                        setStopping(true);
                    }
                }
            }
        }
        return 1000;
    }

    @Override
    public void notify(RenderEvent renderEvent) {
        Graphics g = renderEvent.getSource();
        long milliseconds = (System.nanoTime() - startTime) / (1000 * 1000);
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000*60) % 60);
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;

        g.setColor(color1);
        g.fillRect(5, 5, 285, 77);
        g.setColor(color2);
        //g.setStroke(stroke1);
        g.drawRect(5, 5, 285, 77);
        g.setFont(font1);
        g.drawString("Time Running: " + String.format("%02d:%02d:%02d", hours, minutes, seconds) , 11, 35);
        g.drawString("Bowls Filled (est): " + bowlsFilled, 11, 52);
        g.setFont(font2);
        g.drawString("BOWL FILLER", 95, 21);
        g.setFont(font3);
        g.drawString("Status: " + status, 11, 69);
    }
}




