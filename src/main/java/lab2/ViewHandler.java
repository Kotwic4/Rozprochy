package lab2;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MergeView;
import org.jgroups.View;

import java.util.List;

class ViewHandler extends Thread {
    private JChannel ch;
    private MergeView view;

    ViewHandler(JChannel ch, MergeView view) {
        this.ch = ch;
        this.view = view;
    }

    public void run() {
        System.out.println("ViewHandler");
        List<View> subgroups = view.getSubgroups();
        View tmp_view = subgroups.get(0);
        Address local_addr = ch.getAddress();
        if (!tmp_view.getMembers().contains(local_addr)) {
            System.out.println("Not member of the new primary partition (" + tmp_view + "), will re-acquire the state");
            try {
                ch.getState(null, 30000);
            } catch (Exception ex) {
            }
        } else {
            System.out.println("Not member of the new primary partition (" + tmp_view + "), will do nothing");
        }
    }
}
