package pl.szczepanik.silencio.processors;

import static org.hamcrest.core.StringContains.containsString;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import pl.szczepanik.silencio.api.Converter;
import pl.szczepanik.silencio.api.Format;
import pl.szczepanik.silencio.api.Processor;
import pl.szczepanik.silencio.core.ConverterBuilder;
import pl.szczepanik.silencio.core.ProcessorException;
import pl.szczepanik.silencio.stubs.StubConverter;
import pl.szczepanik.silencio.stubs.WriterStub;
import pl.szczepanik.silencio.utils.ReflectionUtils;
import pl.szczepanik.silencio.utils.ResourceLoader;

/**
 * @author Damian Szczepanik <damianszczepanik@github>
 */
public class JSONProcessorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Writer output;
    private Reader input;

    @Test
    public void shouldFailWhenLoadingInvalidJSONFile() {

        // given
        input = ResourceLoader.loadJsonAsReader("corrupted.json");

        // when
        Processor processor = ConverterBuilder.build(Format.JSON, ConverterBuilder.BLANK);

        // then
        thrown.expect(ProcessorException.class);
        thrown.expectMessage(containsString("Unexpected character"));
        processor.load(input);
    }

    @Test
    public void shouldReportExceptionOnUnsupportedModel() throws Exception {

        // when
        String key = "myKey";
        Object value = new Object();
        JSONProcessor processor = new JSONProcessor();
        processor.setConverters(new Converter[] { new StubConverter() });

        // then
        thrown.expect(ProcessorException.class);
        thrown.expectMessage("Unknown type of the key: " + value.getClass().getName());
        ReflectionUtils.invokeMethod(processor, "processComplex", Void.class, key, value);
    }

    @Test
    public void shouldFailWhenWrittingToInvalidWriter() {

        final String errorMessage = "Don't write into this writter!";

        // given
        input = ResourceLoader.loadJsonAsReader("empty.json");
        output = new WriterStub() {

            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                throw new IOException(errorMessage);
            }
        };

        // when
        Processor processor = ConverterBuilder.build(Format.JSON, ConverterBuilder.BLANK);
        processor.load(input);
        processor.process();

        // then
        thrown.expect(ProcessorException.class);
        thrown.expectMessage(errorMessage);
        processor.write(output);
    }

    @After
    public void closeStreams() {
        IOUtils.closeQuietly(input);
        IOUtils.closeQuietly(output);
    }

}