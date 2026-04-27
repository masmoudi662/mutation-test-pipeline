java
package org.laidu.learn.amqp.rabbitmq.java;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.AMQP.Queue.DeleteOk;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RabbitmqClientDemoTest {

    @Test
    void declareQueue_success() throws IOException, TimeoutException {

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Channel channel = mock(Channel.class);

        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);

        DeclareOk declareOk = mock(DeclareOk.class);
        when(channel.queueDeclare("demo1",true,false,false,null)).thenReturn(declareOk);

        RabbitmqClientDemo rabbitmqClientDemo = new RabbitmqClientDemo(){
            @Override
            public ConnectionFactory connectionFactory() {
                return connectionFactory;
            }

            @Override
            public Connection connection(ConnectionFactory connectionFactory) throws IOException, TimeoutException {
                return connection;
            }
        };

        rabbitmqClientDemo.declareQueue();

        verify(channel).queueDeclare("demo1",true,false,false,null);
    }

    @Test
    void declareQueue_queueExistsWithDifferentParams_deletesAndRecreates() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Channel channel = mock(Channel.class);

        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);

        ShutdownSignalException shutdownSignalException = mock(ShutdownSignalException.class);
        AMQP.Channel.Close close = mock(AMQP.Channel.Close.class);

        when(shutdownSignalException.getReason()).thenReturn(close);
        when(close.getReplyCode()).thenReturn(406);

        IOException ioException = new IOException(shutdownSignalException);

        when(channel.queueDeclare("demo1",true,false,false,null)).thenThrow(ioException);

        DeleteOk deleteOk = mock(DeleteOk.class);
        when(channel.queueDelete("demo1")).thenReturn(deleteOk);

        DeclareOk declareOk = mock(DeclareOk.class);
        when(channel.queueDeclare("demo1",false,false,false,null)).thenReturn(declareOk);


        RabbitmqClientDemo rabbitmqClientDemo = new RabbitmqClientDemo(){
            @Override
            public ConnectionFactory connectionFactory() {
                return connectionFactory;
            }

            @Override
            public Connection connection(ConnectionFactory connectionFactory) throws IOException, TimeoutException {
                return connection;
            }
        };

        rabbitmqClientDemo.declareQueue();

        verify(channel).queueDeclare("demo1",true,false,false,null);
        verify(channel).queueDelete("demo1");
        verify(channel).queueDeclare("demo1",false,false,false,null);
    }

    @Test
    void declareQueue_IOExceptionNotCausedBy406_throwsIOException() throws IOException, TimeoutException {

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Channel channel = mock(Channel.class);

        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);

        IOException ioException = new IOException("Some other IO Exception");

        when(channel.queueDeclare("demo1",true,false,false,null)).thenThrow(ioException);

        RabbitmqClientDemo rabbitmqClientDemo = new RabbitmqClientDemo(){
            @Override
            public ConnectionFactory connectionFactory() {
                return connectionFactory;
            }

            @Override
            public Connection connection(ConnectionFactory connectionFactory) throws IOException, TimeoutException {
                return connection;
            }
        };

        assertThrows(IOException.class, rabbitmqClientDemo::declareQueue);
    }

    @Test
    void declareQueue_ShutdownSignalExceptionNotCausedBy406_throwsIOException() throws IOException, TimeoutException {

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Channel channel = mock(Channel.class);

        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);

        ShutdownSignalException shutdownSignalException = mock(ShutdownSignalException.class);
        AMQP.Channel.Close close = mock(AMQP.Channel.Close.class);

        when(shutdownSignalException.getReason()).thenReturn(close);
        when(close.getReplyCode()).thenReturn(500);

        IOException ioException = new IOException(shutdownSignalException);

        when(channel.queueDeclare("demo1",true,false,false,null)).thenThrow(ioException);

        RabbitmqClientDemo rabbitmqClientDemo = new RabbitmqClientDemo(){
            @Override
            public ConnectionFactory connectionFactory() {
                return connectionFactory;
            }

            @Override
            public Connection connection(ConnectionFactory connectionFactory) throws IOException, TimeoutException {
                return connection;
            }
        };

        assertThrows(IOException.class, rabbitmqClientDemo::declareQueue);
    }
}