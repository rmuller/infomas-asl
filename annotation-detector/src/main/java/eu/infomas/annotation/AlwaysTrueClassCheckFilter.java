package eu.infomas.annotation;

/**
 * Default implementation of {@code ClassCheckFilter} always returning true.
 * Plugged to provide full compatibility with previous code.
 *
 * @author <a href="mailto:cedric@gatay.fr">Cedric Gatay</a>
 * @since annotation-detector 3.0.2
 */
final class AlwaysTrueClassCheckFilter implements AnnotationDetector.ClassCheckFilter{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEligibleForScanning(final String fileName) {
        return true;
    }
}
