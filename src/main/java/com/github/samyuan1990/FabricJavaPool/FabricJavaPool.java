/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.github.samyuan1990.FabricJavaPool;

import java.io.File;

import com.github.samyuan1990.FabricJavaPool.impl.FabricConnectionImpl;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

public class FabricJavaPool extends GenericObjectPool<FabricConnectionImpl> {

    public FabricJavaPool(User appUser, String channel) {
        super(new ChannelPoolFactory(new FabricJavaPoolConfig().getConfigNetworkPath(), appUser, channel), new FabricJavaPoolConfig());
    }

    public FabricJavaPool(User appUser, String channel, FabricJavaPoolConfig config) {
        super(new ChannelPoolFactory(config.getConfigNetworkPath(), appUser, channel), config);
    }

    private static class ChannelPoolFactory extends BasePooledObjectFactory<FabricConnectionImpl> {

        private String config_network_path = "";
        private User appUser;
        private String channel = "";

        ChannelPoolFactory(String configNetworkPath, User appUser, String channel) {
            this.config_network_path = configNetworkPath;
            this.appUser = appUser;
            this.channel = channel;
        }

        @Override
        public FabricConnectionImpl create() throws Exception {
            FabricConnectionImpl myConnection;
            CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
            HFClient hfclient = HFClient.createNewInstance();
            hfclient.setCryptoSuite(cryptoSuite);
            NetworkConfig networkConfig = NetworkConfig.fromJsonFile(new File(config_network_path));
            hfclient.setUserContext(appUser);
            hfclient.loadChannelFromConfig(channel, networkConfig);
            Channel myChannel = hfclient.getChannel(channel);
            myChannel.initialize();
            myConnection = new FabricConnectionImpl(hfclient, myChannel, appUser);
            return myConnection;
        }

        @Override
        public PooledObject<FabricConnectionImpl> wrap(FabricConnectionImpl obj) {
            return new DefaultPooledObject<>(obj);
        }

    }
}
