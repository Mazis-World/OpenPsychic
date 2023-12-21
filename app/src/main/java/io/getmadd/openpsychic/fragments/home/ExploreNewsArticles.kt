package io.getmadd.openpsychic.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.getmadd.openpsychic.databinding.FragmentExploreBinding
import io.getmadd.openpsychic.databinding.FragmentNewsArticleBinding

class ExploreNewsArticles: Fragment() {
    private lateinit var _binding: FragmentNewsArticleBinding

    private val binding get() = _binding
    val psychicsHealthArticle = """
    In the fast-paced rhythm of modern life, where stress and uncertainty often take center stage, individuals are increasingly turning to alternative avenues for mental and emotional well-being. Among these mystical pathways, the engagement with psychics emerges as a beacon of hope and healing, offering profound benefits for overall health.

    1. Personalized Insights for Inner Harmony:
    Psychics serve as guides on the journey to self-discovery, offering personalized insights into one's innermost thoughts and emotions. This unique connection fosters a deeper understanding of oneself, paving the way for enhanced emotional intelligence and inner harmony.

    2. Stress Reduction and Emotional Resilience:
    Psychic sessions are renowned for their stress-alleviating properties. Through a combination of intuitive guidance and empathetic support, psychics assist individuals in navigating life's challenges with resilience and emotional fortitude, reducing the detrimental impact of stress on mental health.

    3. Holistic Approaches to Mental Wellness:
    Unlike traditional therapeutic methods, psychics often adopt holistic approaches that extend beyond the realms of psychology. They delve into spiritual and metaphysical dimensions, acknowledging the interconnectedness of mind, body, and spirit, leading to a more comprehensive and balanced approach to mental wellness.

    4. Validation and Affirmation:
    The power of validation cannot be overstated. Psychics, through their unique gifts, provide a space where individuals feel heard and understood. This affirmation has the potential to instill a sense of confidence, reducing self-doubt and fostering positive self-esteem.

    5. Navigating Life Transitions:
    Life transitions, whether personal or professional, can be challenging to navigate. Psychics offer valuable insights and guidance during pivotal moments, assisting individuals in making informed decisions and embracing new opportunities with clarity and purpose.

    6. Cultural and Global Perspectives:
    Engaging with psychics allows individuals to access a wealth of cultural and global perspectives. As the world becomes increasingly interconnected, this exposure to diverse wisdom enhances one's understanding of life, fostering a more open-minded and tolerant approach to the complexities of the human experience.

    In a world where holistic well-being is paramount, the inclusion of psychics in one's mental health toolkit offers a unique and transformative approach. Beyond the mystique, psychics play a crucial role in nurturing emotional resilience, fostering self-discovery, and promoting a holistic understanding of mental wellness. Whether seeking validation, stress relief, or a guiding light through life's transitions, the healing power of psychics extends far beyond the ordinary, inviting individuals to explore new dimensions of well-being.
"""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentArticleNewsTextView.text = psychicsHealthArticle
        binding.buttonSecond.setOnClickListener{
            findNavController().popBackStack()
        }
    }



}