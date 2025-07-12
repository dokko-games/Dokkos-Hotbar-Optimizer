# Dokko's Hotbar Optimizer

Dokko's Hotbar Optimizer is a client-side mod designed to refine how Minecraft handles hotbar slot changes.

## What does the mod do?

In vanilla Minecraft, a small but impactful delay exists: after you change your active hotbar slot, your client waits until the next game tick (up to 50 milliseconds) before sending that update to the server. This mod eliminates that artificial client-side delay, ensuring your hotbar updates are transmitted immediately upon action.

This fundamental change brings noticeable improvements to several key areas of gameplay:
- Ghost Totem Reduction:
The mod significantly shrinks the window during which a totem might fail to activate due to server-client desynchronization. By sending your hotbar change packet instantly, the server is informed of your totem's position much sooner.
  - Low Ping (e.g., 20ms round trip): The potential "ghost totem window" can be reduced by approximately 71%. This means a theoretical 35ms window (25ms average client delay + 10ms ping) shrinks to just 10ms (ping alone).
  - High Ping (e.g., 150ms round trip): While ping remains the dominant factor, the mod still offers a tangible reduction of the client-side delay. The "ghost totem window" can be reduced by approximately 25%, cutting a 100ms window (25ms average client delay + 75ms ping) down to 75ms (ping alone).
  
- Attribute Swapping Improvement:
The consistency and responsiveness of rapid hotbar swaps, crucial for advanced combat techniques like breach or axe swapping, are enhanced. The server receives your slot change information faster, allowing for more reliable execution of complex combos.
  - Low Ping: The time for the server to register your swap after your client action is improved by approximately 71%.
  - High Ping: The time for the server to register your swap after your client action is improved by approximately 25%.

  
Using this mod provides a more precise and reliable hotbar experience by removing an useless delay from the client's packet sending mechanism. This leads to more predictable outcomes in critical situations and overall smoother interaction with your hotbar.

## How do I turn it on / off?
The mod comes with the command

```
/hotbar-optimizer <on/off>
```
as well as
```
/hotbar-optimizer
```
which just tells you if the mod is currently enabled
