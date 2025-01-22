package ru.yandex.practicum.deserializer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
@RequiredArgsConstructor
public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private final Schema schema;
    private final DecoderFactory decoderFactory = DecoderFactory.get();
    private final DatumReader<T> reader;

    public BaseAvroDeserializer(Schema schema) {
        this.schema = schema;
        this.reader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            log.warn("Получены null данные для десериализации из топика [{}]", topic);
            return null;
        }
        try {
            Decoder decoder = decoderFactory.binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (Exception e) {
            log.error("Ошибка десериализации данных из топика [{}]", topic, e);
            throw new SerializationException("Ошибка десериализации данных из топика [" + topic + "]", e);
        }
    }
}