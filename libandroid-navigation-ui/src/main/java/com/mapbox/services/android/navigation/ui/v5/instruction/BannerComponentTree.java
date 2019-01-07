package com.mapbox.services.android.navigation.ui.v5.instruction;

import android.support.annotation.NonNull;

import com.mapbox.api.directions.v5.models.BannerComponents;

import java.util.ArrayList;
import java.util.List;

public class BannerComponentTree {
  private final NodeCreator[] nodeCreators;
  private List<BannerComponentNode> bannerComponentNodes;

  /**
   * Creates a master coordinator to make sure the coordinators passed in are used appropriately
   *
   * @param nodeCreators coordinators in the order that they should process banner components
   */
  BannerComponentTree(@NonNull List<BannerComponents> bannerComponents, NodeCreator... nodeCreators) {
    this.nodeCreators = nodeCreators;
    bannerComponentNodes = parseBannerComponents(bannerComponents);
  }

  /**
   * Parses the banner components and processes them using the nodeCreators in the order they
   * were originally passed
   *
   * @param bannerComponents to parse
   * @return the list of nodes representing the bannerComponents
   */
  private List<BannerComponentNode> parseBannerComponents(List<BannerComponents> bannerComponents) {
    int length = 0;
    List<BannerComponentNode> bannerComponentNodes = new ArrayList<>();

    for (BannerComponents components : bannerComponents) {
      BannerComponentNode node = null;

      for (NodeCreator nodeCreator : nodeCreators) {
        if (nodeCreator.isNodeType(components)) {
          node = nodeCreator.setupNode(components, bannerComponentNodes.size(), length);
          break;
        }
      }

      if (node != null) {
        bannerComponentNodes.add(node);
        length += components.text().length() + 1;
      }
    }

    return bannerComponentNodes;
  }

  /**
   * Loads the instruction into the given text view. If things have to be done in a particular order,
   * the coordinator methods preProcess and postProcess can be used. PreProcess should be used to
   * load text into the textView (so there should only be one coordinator calling this method), and
   * postProcess should be used to make changes to that text, i.e., to load images into the textView.
   *
   * @param textView in which to load text and images
   */
  void loadInstruction(InstructionTextView textView) {
    for (NodeCreator nodeCreator : nodeCreators) {
      nodeCreator.preProcess(textView, bannerComponentNodes);
    }

    for (NodeCreator nodeCreator : nodeCreators) {
      nodeCreator.postProcess(textView, bannerComponentNodes);
    }
  }
}
