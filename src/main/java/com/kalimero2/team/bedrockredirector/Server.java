package com.kalimero2.team.bedrockredirector;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v618.Bedrock_v618;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class Server {

    public static final BedrockCodec CODEC = Bedrock_v618.CODEC;
    public static Properties properties;
    public static PaletteManager paletteManager;

    public static void main(String[] args) {

        properties = new Properties();

        try {
            FileInputStream input = new FileInputStream("server.properties");
            properties.load(input);
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find properties file, creating one");

            properties.setProperty("modt", "Modt");
            properties.setProperty("submodt", "Submodt");
            properties.setProperty("hostname", "0.0.0.0");
            properties.setProperty("port", "19132");
            properties.setProperty("transferAddress", "example.com");
            properties.setProperty("transferPort", "19132");

            try {
                properties.store(new FileOutputStream("server.properties"), null);
            } catch (IOException ex) {
                // Could not create properties
                System.err.println("Could not create properties file. Error: "+ ex.getMessage());
            }
        } catch (IOException e) {
            // Could not load properties
            System.out.println("Could not load properties file. Error: "+ e.getMessage());
        }

        String modt = properties.getProperty("modt");
        String submodt = properties.getProperty("submodt");
        String hostname = properties.getProperty("hostname");
        int port = Integer.parseInt(properties.getProperty("port"));
        String transferAddress = properties.getProperty("transferAddress");
        int transferPort = Integer.parseInt(properties.getProperty("transferPort"));

        paletteManager = new PaletteManager();
        InetSocketAddress bindAddress = new InetSocketAddress(hostname, port);


        BedrockPong pong = new BedrockPong()
                .edition("MCPE")
                .motd(modt)
                .playerCount(0)
                .maximumPlayerCount(1)
                .gameType("Survival")
                .subMotd(submodt)
                .protocolVersion(CODEC.getProtocolVersion());

        new ServerBootstrap()
                .channelFactory(RakChannelFactory.server(NioDatagramChannel.class))
                .option(RakChannelOption.RAK_ADVERTISEMENT, pong.toByteBuf())
                .group(new NioEventLoopGroup())
                .childHandler(new BedrockServerInitializer() {
                    @Override
                    protected void initSession(BedrockServerSession session) {
                        session.setCodec(CODEC);
                        session.setPacketHandler(new PacketHandler(session, transferAddress, transferPort));
                    }
                })
                .bind(bindAddress)
                .syncUninterruptibly();

        System.out.println("Server started on " + hostname + ":" + port);
    }

}
