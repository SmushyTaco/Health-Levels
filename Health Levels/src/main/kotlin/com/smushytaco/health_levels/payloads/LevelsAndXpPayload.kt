package com.smushytaco.health_levels.payloads
import com.smushytaco.health_levels.abstractions.HealthMethods
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
class LevelsAndXpPayload(val value: List<Int>) : CustomPayload {
    override fun getId() = payloadId
    companion object {
        val payloadId = CustomPayload.Id<LevelsAndXpPayload>(HealthMethods.CONFIG_PACKET_IDENTIFIER)
        val CODEC: PacketCodec<RegistryByteBuf, LevelsAndXpPayload> = PacketCodec.of({ payload, buf -> buf.writeIntList(IntList.of(*payload.value.toIntArray())) }) { buf -> LevelsAndXpPayload(buf.readIntList()) }
    }
}