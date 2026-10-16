package com.smartbanksearch;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SmartBankSearchTestLauncher
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(SmartBankSearchPlugin.class);
        RuneLite.main(args);
    }
}
