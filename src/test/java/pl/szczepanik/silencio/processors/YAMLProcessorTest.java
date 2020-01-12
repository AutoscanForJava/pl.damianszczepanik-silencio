package pl.szczepanik.silencio.processors;

import org.junit.Test;
import pl.szczepanik.silencio.GenericTest;
import pl.szczepanik.silencio.api.Format;
import pl.szczepanik.silencio.api.Processor;
import pl.szczepanik.silencio.core.Builder;
import pl.szczepanik.silencio.core.Configuration;
import pl.szczepanik.silencio.core.Execution;
import pl.szczepanik.silencio.core.ProcessorException;
import pl.szczepanik.silencio.decisions.PositiveDecision;
import pl.szczepanik.silencio.mocks.WriterCrashOnWrite;
import pl.szczepanik.silencio.utils.ResourceLoader;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class YAMLProcessorTest extends GenericTest {

    @Test
    public void shouldReturnPassedFormat() {

        // given
        Processor processor = new YAMLProcessor();

        // when
        Format format = processor.getFormat();

        // then
        assertThat(format).isEqualTo(Format.YAML);
    }

    @Test
    public void shouldLoadYAMLFileOnRealLoad() {

        // given
        YAMLProcessor processor = new YAMLProcessor();
        input = ResourceLoader.loadYamlAsReader("suv.yaml");
        String refInput = ResourceLoader.loadYamlAsString("suv.yaml");
        output = new StringWriter();

        // when
        processor.load(input);

        // then
        processor.realWrite(output);
        assertThat(refInput).isEqualToNormalizingNewlines(output.toString());
    }

    @Test
    public void shouldFailWhenLoadingInvalidYAMLFile() {

        // given
        Processor processor = new YAMLProcessor();
        Execution execution = new Execution(new PositiveDecision(), Builder.BLANK);
        input = ResourceLoader.loadYamlAsReader("corrupted.yaml");

        // when
        processor.setConfiguration(new Configuration(execution));

        // then
        thrown.expect(ProcessorException.class);
        thrown.expectMessage(containsString("Cannot construct instance of"));
        processor.load(input);
    }

    @Test
    public void shouldFailWhenWritingToInvalidWriter() {

        final String errorMessage = "Don't write into this writer!";

        // given
        YAMLProcessor processor = new YAMLProcessor();
        Execution execution = new Execution(new PositiveDecision(), Builder.BLANK);
        processor.setConfiguration(new Configuration(execution));
        input = ResourceLoader.loadYamlAsReader("empty.yaml");
        output = new WriterCrashOnWrite(errorMessage);

        // when
        processor.load(input);
        processor.realProcess();

        // then
        thrown.expect(ProcessorException.class);
        thrown.expectMessage(errorMessage);
        processor.realWrite(output);
    }
}