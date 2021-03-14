package com.velocitypowered.proxy.console;

import static com.velocitypowered.api.permission.PermissionFunction.ALWAYS_TRUE;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class VelocityConsoleCommandSource implements ConsoleCommandSource {

  private static final Logger logger = LogManager.getLogger(VelocityConsoleCommandSource.class);

  private PermissionFunction permissionFunction = ALWAYS_TRUE;

  @Override
  public void sendMessage(net.kyori.text.Component component) {
    logger.info(net.kyori.text.serializer.legacy.LegacyComponentSerializer.legacy()
        .serialize(component));
  }

  @Override
  public void sendMessage(@NonNull Identity identity, @NonNull Component message) {
    logger.info(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
        .serialize(message));
  }

  @Override
  public @NonNull Tristate getPermissionValue(@NonNull String permission) {
    return this.permissionFunction.getPermissionValue(permission);
  }

  /**
   * Sets up {@code System.out} and {@code System.err} to redirect to log4j.
   */
  public void setupStreams() {
    System.setOut(IoBuilder.forLogger(logger).setLevel(Level.INFO).buildPrintStream());
    System.setErr(IoBuilder.forLogger(logger).setLevel(Level.ERROR).buildPrintStream());
  }

  /**
   * Sets up permissions for the console.
   * @param eventManager the event manager
   */
  public void setupPermissions(EventManager eventManager) {
    PermissionsSetupEvent event = new PermissionsSetupEvent(this, s -> ALWAYS_TRUE);
    // we can safely block here, this is before any listeners fire
    this.permissionFunction = eventManager.fire(event).join().createFunction(this);
  }

}
