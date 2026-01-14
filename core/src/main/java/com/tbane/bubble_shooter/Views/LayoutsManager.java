package com.tbane.bubble_shooter.Views;

import java.util.ArrayList;

public class LayoutsManager {
    public static ArrayList<Layout> layouts = new ArrayList<>();

    public static void add(Layout layout){
        layouts.add(layout);
    }

    public static Layout back() {
        if (layouts.isEmpty()) return null;
        return layouts.get(layouts.size() - 1);
    }

    public static void pop_back(){
        if(layouts.isEmpty())
            return;

        LayoutsManager.layouts.remove(layouts.size()-1);

    }
}
