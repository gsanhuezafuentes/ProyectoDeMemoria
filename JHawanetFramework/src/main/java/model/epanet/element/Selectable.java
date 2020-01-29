package model.epanet.element;

/**
 * This is a marker interface that indicates that an element of the network is
 * selectable. Network elements marked with this interface indicate that the
 * user in the GUI can click on them. The only class that implement this
 * interface are Link, node and his subclasses. It is because it are the only
 * showed in GUI.
 *
 */
public interface Selectable {

}
