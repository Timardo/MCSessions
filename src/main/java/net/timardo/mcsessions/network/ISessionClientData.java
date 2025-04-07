package net.timardo.mcsessions.network;

import java.io.Serializable;

/**
 * Implement this interface to create your own type that will be carried from client to server and vice versa.
 * @author Timardo
 *
 */
public interface ISessionClientData extends Serializable {

	public static final long serialVersionUID = 1L;
}
