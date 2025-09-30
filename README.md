# RMI-MQ-Bio-System

> Architecture and workflow diagrams are available on request.


A distributed Java application built using RMI for synchronous client-server communication and JMS (via WildFly) for asynchronous audit logging. Users can add, update, and list biographies, which are then sent to a message queue and logged by a dedicated logger service.

## 1. Development Tools and Technologies

- **IntelliJ IDEA** – Used as the primary IDE for writing and managing Java code.
- **Java RMI & JMS APIs** – Used to implement synchronous remote method invocation and asynchronous message-based communication.
- **WildFly (Jakarta EE server)** – Configured as the message queue broker to manage JMS resources like connection factories and queues.
- **Jakarta EE (JMS libraries)** – Included to support messaging features within the WildFly environment.
- **StarUML** – Used to model system architecture and generate class/component diagrams.

> While the system simulates a distributed architecture, all components (client, RMI server, JMS consumer) were deployed locally on the same machine using separate ports and services. This setup allowed realistic simulation of remote communication without requiring multiple physical hosts.

## 2. Package Structure

The system is structured into four core packages:

- `client`
  - `ClientApp`: Connects to the RMI registry, invokes remote methods, and handles console-based user input.

- `server`
  - `BioServiceImpl`: Implements the logic defined in the RMI remote interface.
  - `RMIServer`: Sets up and binds the remote object to the RMI registry.

- `mq`
  - `MQProducer`: Sends `BioDetails` objects to a JMS queue managed by WildFly.
  - `AuditLogger`: Listens to the queue and logs incoming messages to `audit.log`.

- `shared`
  - `BioService`: Remote interface defining available RMI operations.
  - `BioDetails`: Serializable data transfer object used for communication between client and server.

## 3. System Architecture

This project demonstrates a hybrid distributed system using **Java RMI** for synchronous communication and **JMS (WildFly)** for asynchronous messaging. The system is organized into four logical components:

- **Client**: `ClientApp` connects to the RMI registry and invokes remote methods.
- **Server**: `RMIServer` and `BioServiceImpl` provide the remote services.
- **Message Queue (mq)**: `MQProducer` sends messages to a JMS queue; `AuditLogger` consumes them.
- **Shared**: Contains `BioService` (RMI interface) and `BioDetails` (data object).

### 3.1 Communication Flow

1. `ClientApp` retrieves the remote `BioService` interface from the RMI registry.
2. Remote calls such as `addBio()` are sent to `BioServiceImpl` via RMI.
3. `BioServiceImpl` uses `MQProducer` to forward `BioDetails` objects to `WildFlyQueue`.
4. `AuditLogger` listens to the queue and logs incoming messages.
5. RMI provides synchronous interaction; JMS provides asynchronous logging for audit.

### 3.2 System Diagram

![](./img/class-diagram-rmi-mq-bio.jpg)

In addition, the following logical components are represented to complete the flow:

- `RegistryConnection` – simulates the RMI lookup mechanism.
- `WildFlyQueue` – symbolizes the JMS queue hosted by WildFly.
- `java.rmi.Remote` – represents the base interface required for RMI.

### 3.3 Running the Application (RMI)

To test the RMI functionality, follow these steps:

1. **Start WildFly** using the command:

    ```  
    standalone.bat -c standalone-full.xml  
    ```  
   This enables the JMS subsystem and admin console (WildFly must be installed and configured beforehand).

2. **Launch `RMIServer`** from IntelliJ. This registers and exposes the `BioService` over RMI.

3. **Run `ClientApp`**, which presents a console-based menu:

    ```  
    1. Add new bio    
    2. Update existing bio    
    3. List all bios    
    E. Exit    
    ```  

4. Initially, option `3` (List all bios) displays two default entries preloaded in `BioServiceImpl`:

    ```text  
    bios.add(new BioDetails("Elena", "Works in digital marketing."));  
    bios.add(new BioDetails("John", "Specialist in client communications."));  
    ```  
5. The program loops after each operation until the user selects `E` to exit.

#### 3.3.1 Example Flow

- Added two new bios: **Daniel** and **Laura**
- Updated Daniel’s bio content
- Listed all bios again to confirm the changes

This produces output similar to:

```  
CLIENT: Bios list:  
- [timestamp] Updated bio entry for Elena: "Works in digital marketing."  
- [timestamp] Updated bio entry for John: "Specialist in client communications."  
- [timestamp] Added bio entry for Daniel: "Director of marketing innovation."  
- [timestamp] Added bio entry for Laura: "Software engineer with AI focus."  
```  

Each client action triggers a remote method (`addBio()`, `updateBio()`, or `listBios()`) on the server. These are confirmed by corresponding logs in the `RMIServer` console, verifying that all transactions are executed via RMI as intended.

These are two images of console output from `RMIServer` (left) and `ClientApp` (right):

- After selecting option 3 to list the default bios    
  ![Default bios listing](./img/option_3.png)

- After adding two bios and updating one entry    
  ![Full RMI session](./img/adding_two_bios_updating_one.png)

### 3.4 Message Queue Delivery Confirmation via WildFly

To verify message durability, several RMI operations were executed **before** starting the `AuditLogger` or even the WildFly server. During this phase, the `RMIServer` was restarted multiple times to capture clean screenshots and perform repeated tests.

Eventually, two `addBio` entries were made for **Daniel** and **Laura**, followed by an `updateBio` for Daniel, all while WildFly was still offline.

Once WildFly and `AuditLogger` were started, all previously published messages, including those generated across multiple RMI restarts, were immediately retrieved and processed from the queue in the correct execution order. This confirms the durability of the queue and verifies that no messages were lost.

#### 3.4.1 Console Output Screenshots:

- Durable message retrieval after delayed `AuditLogger` startup:    
  ![Durable Queue Consumption](./img/confirming_retrieval_while_offline.png)


- Live message delivery while `AuditLogger` was running:    
  ![Live Queue Delivery](./img/live_message_delivery.png)

## 4. Visual Architecture and Code Walkthrough

To further illustrate the system’s design and execution flow, the following annotated diagrams highlight key components and their interactions:

- **Client–Server Connectivity via RMI**  
  ![Client–Server Connectivity](./img/client_server_connectivity_rmi.png)

- **DTO → Interface → Implementation Mapping**  
  ![DTO to Interface to Implementation](./img/dto_to_interfece_to_implementation_mapping.png)

- **Synchronous Invocation of MQProducer from BioServiceImpl**  
  ![Synchronous Invocation](./img/synchronous_invocation_of_mqproducer.send_from_bioserviceimpl.png)

- **End-to-End Asynchronous Communication**  
  ![End-to-End Asynchronous Communication](./img/end_to_end_assynchronous_communication.png)

- **RMI Service Export and Interface Exposure**  
  ![RMI Service Export](./img/rmi_service_export_and_interface_exposure.png)

- **MQProducer to AuditLogger via WildFly Communication Path**  
  ![MQProducer to AuditLogger](./img/mqproducer_to_auditlogger_via_wildfly_communication_path.png)
