package team.lodestar.lodestone.systems.network;


public abstract class OneSidedPayloadData extends LodestoneNetworkPayloadData {

    public abstract void handle(final IPayloadContext context);
}