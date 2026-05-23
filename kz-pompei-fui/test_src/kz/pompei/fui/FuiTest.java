package kz.pompei.fui;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;

public class FuiTest {

  @Test
  public void application() {

    Fui fui = Fui.builder().rootDir(Paths.get("build/application")).build();

    List<Disconnector> disconnectors = new ArrayList<>();

    disconnectors.add(fui.button("Hello1").click(() -> System.out.println("LZK7eKsZD1 :: Clicked BTN hello1")));
    disconnectors.add(fui.button("Hello2").click(() -> System.out.println("kIp3ygQ7PT :: Clicked BTN hello2")));

    FuiEditor stone = fui.editor("Stone");

    disconnectors.add(stone.change(() -> System.out.println("017Irf4fGM :: Stone changed to `" + stone.value.get() + "`")));

    disconnectors.add(fui.button("SetStoneTo_HELLO").click(() -> stone.value.set("HELLO")));
    disconnectors.add(fui.button("SetStoneTo_ByBy").click(() -> stone.value.set("ByBy")));

    disconnectors.add(fui.button("ReadStone").click(() -> System.out.println("ReadStone :: Stone value is `" + stone.value.get() + "`")));

    System.out.println("dgq18MmsQO :: Application started");

    FuiCheckbox done = fui.checkbox("Done");

    disconnectors.add(done.change(() -> System.out.println("Gi7f5ywo9t :: Checkbox changed to " + done.value.is())));

    disconnectors.add(fui.button("ChangeDoneTo_YES").click(() -> done.value.set(true)));
    disconnectors.add(fui.button("ChangeDoneTo_NO").click(() -> done.value.set(false)));
    disconnectors.add(fui.button("ReadDone").click(() -> System.out.println("Szj05g0Jkw :: Checkbox value is " + done.value.is())));

    fui.go();

    disconnectors.forEach(Disconnector::disconnect);

    stone.remove();
    done.remove();

    System.out.println("717SnB3Dxk :: Application exited");

  }
}
